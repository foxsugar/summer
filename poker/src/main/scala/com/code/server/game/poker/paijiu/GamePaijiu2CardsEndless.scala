package com.code.server.game.poker.paijiu

import com.code.server.game.room.kafka.MsgSender

import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2017/10/25.
  */
class GamePaijiu2CardsEndless extends GamePaijiuEndless {


  /**
    * 发牌
    */
  override protected def deal(): Unit = {

    val slidList = cards.sliding(2, 2).toList
    var count = 0
    for (playerInfo <- playerCardInfos.values) {
      playerInfo.cards ++= slidList(count)
      count += 1
      //发牌通知
      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
    }

  }


  /**
    * 是否所有人都开牌
    *
    * @return
    */
  override protected def isAllPlayerOpen(): Boolean = {
    playerCardInfos.count { case (_, playerInfo) => playerInfo.group1 == null } == 0
  }


  /**
    * 检测开牌是否合法
    *
    * @param playerCardInfo
    * @param group1
    * @param group2
    * @return
    */
  override protected def checkOpen(playerCardInfo: PlayerCardInfoPaijiu, group1: String, group2: String): Boolean = {
    val allCard = Array.concat(group1.split(",")).map(card => card.toInt).toList

    //开的牌和拥有的牌相同
    val isSame = playerCardInfo.cards.diff(allCard).isEmpty
    if (!isSame) return false
    true
  }

  /**
    * 结算
    */
  override def compute(): Unit = {
    val banker = playerCardInfos(bankerId)
    var winUsers: List[PlayerCardInfoPaijiu] = List()
    val mix8Score = getGroupScoreByName(MIX_8)
    var resultSet: Set[Int] = Set()
    playerCardInfos.foreach { case (uid, other) =>
      if (uid != bankerId) {
        val bankerScore1 = getGroupScore(banker.group1)
        val otherScore1 = getGroupScore(other.group1)
        var result: Int = 0
        if (bankerScore1 >= otherScore1) result += 1
        if (bankerScore1 < otherScore1) result -= 1
        resultSet = resultSet.+(result)
        //庄家赢
        if (result > 0) {
          val changeScore = other.getBetScore(bankerScore1 >= mix8Score)
          banker.addScore(roomPaijiu, changeScore)
          other.addScore(roomPaijiu, -changeScore)
          roomPaijiu.bankerScore += changeScore
          roomPaijiu.addUserSocre(banker.userId, changeScore)
          roomPaijiu.addUserSocre(other.userId, -changeScore)
          other.winState = LOSE

          logger.info("庄家赢得钱: " + changeScore)

        } else if (result < 0) {
          other.winState = WIN
          winUsers = winUsers.+:(other)
        } else {
          logger.info("和了")
        }

      }
    }

    //全赢或全输
    if (resultSet.size == 1) {
      val bankerStatiseics = this.roomPaijiu.getRoomStatisticsMap.get(bankerId)
      if (resultSet.contains(WIN)) bankerStatiseics.winAllTime += 1
      if (resultSet.contains(LOSE)) bankerStatiseics.loseAllTime += 1
    }

    //排序后的
    val sortedUsers = winUsers.sortWith(compareByScore)
    for (playerInfo <- sortedUsers) {
      val score2 = getGroupScore(playerInfo.group1)
      //庄家应该输的钱
      val bankerLoseScore = playerInfo.getBetScore(score2 >= mix8Score)
      val loseScore = if (bankerLoseScore > banker.score) banker.score else bankerLoseScore
      logger.info("应输的钱: " + bankerLoseScore)
      logger.info("实际的钱: " + loseScore)
      logger.info("庄家的钱: " + banker.score)

      //分数变化
      banker.addScore(roomPaijiu, -loseScore.toInt)
      roomPaijiu.bankerScore -= loseScore.toInt
      playerInfo.addScore(roomPaijiu, loseScore.toInt)
      roomPaijiu.addUserSocre(banker.userId, -loseScore)
      roomPaijiu.addUserSocre(playerInfo.userId, loseScore)
    }
  }



  /**
    * 排序
    *
    * @param playerInfo1
    * @param playerInfo2
    * @return
    */
  override def compareByScore(playerInfo1: PlayerCardInfoPaijiu, playerInfo2: PlayerCardInfoPaijiu): Boolean = {
    val playerScore1 = getGroupScore(playerInfo1.group1)
    val playerScore2 = getGroupScore(playerInfo2.group1)


    if (playerScore1 > playerScore2) {
      true
    } else if (playerScore1 == playerScore2) {
      if (playerInfo1.userId >= playerInfo2.userId) true else false
    } else {
      false
    }

  }
}
