package com.code.server.game.poker.paijiu

import java.lang.Long
import java.util

import com.code.server.game.room.Room

/**
  * Created by sunxianping on 2017/8/3.
  */
class GamePaijiuEndless extends GamePaijiu {

  /**
    * 开始游戏
    *
    * @param users
    * @param room
    */
  override def startGame(users: util.List[Long], room: Room): Unit = {
    roomPaijiu = room.asInstanceOf[RoomPaijiu]
    //实例化玩家
    initPlayer()
    //码牌
    initCards()

    bankerId = roomPaijiu.getBankerId

    room.getCurGameNumber match {
      case 1 => fightForBankerStart()
      case _ if room.getCurGameNumber % 2 == 0 =>betStart()
      case _ => bankerBreakStart()
    }
  }

  /**
    * 下注状态
    */
  override def betStart() = {
    //庄家设置分数
    val banker = playerCardInfos(bankerId)
    banker.score = roomPaijiu.bankerScore
    super.betStart()

  }

  /**
    * 牌局结束
    */
  override def gameOver(): Unit = {
    compute()
    sendResult()
    this.roomPaijiu.clearReadyStatus(true)
    val banker = playerCardInfos(bankerId)
    if (banker.score <= 0 || banker.score >= roomPaijiu.bankerInitScore * roomPaijiu.getUsers.size()) {
      sendFinalResult()
    }
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
        val bankerScore2 = getGroupScore(banker.group2)
        val otherScore1 = getGroupScore(other.group1)
        val otherScore2 = getGroupScore(other.group2)
        var result: Int = 0
        if (bankerScore1 > otherScore1) result += 1
        if (bankerScore1 < otherScore1) result -= 1
        if (bankerScore2 > otherScore2) result += 1
        if (bankerScore2 < otherScore2) result -= 1
        resultSet = resultSet.+(result)
        //庄家赢
        if (result > 0) {
          val changeScore = other.getBetScore(bankerScore2 >= mix8Score)
          banker.score += changeScore
          other.score -= changeScore
          roomPaijiu.addUserSocre(banker.userId, changeScore)
          roomPaijiu.addUserSocre(other.userId, -changeScore)
          other.winState = LOSE

        } else if (result < 0) {
          other.winState = WIN
          winUsers = winUsers.+:(other)
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
      val score2 = getGroupScore(playerInfo.group2)
      //庄家应该输的钱
      val bankerLoseScore = playerInfo.getBetScore(score2 >= mix8Score)
      val loseScore = if (bankerLoseScore > banker.score) banker.score else bankerLoseScore
      logger.info("应输的钱: " + bankerLoseScore)
      logger.info("实际的钱: " + loseScore)
      logger.info("庄家的钱: " + banker.score)

      //分数变化
      banker.score -= loseScore
      playerInfo.score += loseScore
      roomPaijiu.addUserSocre(banker.userId, -loseScore)
      roomPaijiu.addUserSocre(playerInfo.userId, loseScore)
    }
  }

  /**
    * 获取牌得分
    *
    * @param playerInfo
    * @return
    */
  protected def getCardScore(playerInfo: PlayerCardInfoPaijiu): (Int, Int) = {
    val score1 = getGroupScore(playerInfo.group1)
    val score2 = getGroupScore(playerInfo.group2)
    (score1, score2)
  }


  /**
    * 排序
    *
    * @param playerInfo1
    * @param playerInfo2
    * @return
    */
  protected def compareByScore(playerInfo1: PlayerCardInfoPaijiu, playerInfo2: PlayerCardInfoPaijiu): Boolean = {
    val playerScore1 = getCardScore(playerInfo1)
    val playerScore2 = getCardScore(playerInfo2)

    if (playerScore1._2 > playerScore2._2) {
      true
    } else if (playerScore1._2 == playerScore2._2) {
      if (playerScore1._1 > playerScore2._1) true else false
    } else {
      false
    }
  }

}
