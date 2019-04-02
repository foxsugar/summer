package com.code.server.game.poker.paijiu

import java.lang.Long
import java.util

import com.code.server.constant.data.DataManager
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
      case 1 => {
        fightForBankerStart()
      }
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
    roomPaijiu.getUserScores.put(bankerId,banker.score)
    super.betStart()

  }

  /**
    * 牌局结束
    */
  override def gameOver(): Unit = {
    compute()
    sendResult()
    genRecord()
    this.roomPaijiu.clearReadyStatus(true)
    val banker = playerCardInfos(bankerId)
//    if (banker.score <= 0 || banker.score >= roomPaijiu.bankerInitScore * roomPaijiu.getUsers.size()) {
    var max:Double = 600
    val roomData = DataManager.data.getRoomDataMap.get(this.roomPaijiu.getGameType)
    if (roomData != null) {
      if (roomData.getMaxBet != 0) {
        max = roomData.getMaxBet
      }
    }
    if (banker.score <= 0 || banker.score >= max || this.roomPaijiu.isAceRoomOver()) {
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
        if (bankerScore1 >= otherScore1) result += 1
        if (bankerScore1 < otherScore1) result -= 1
        if (bankerScore2 >= otherScore2) result += 1
        if (bankerScore2 < otherScore2) result -= 1
        resultSet = resultSet.+(result)
        //庄家赢
        if (result > 0) {
          val changeScore = other.getBetScore(bankerScore2 >= mix8Score)
          banker.addScore(roomPaijiu,changeScore)
          other.addScore(roomPaijiu,-changeScore)
          roomPaijiu.bankerScore += changeScore
          roomPaijiu.addUserSocre(banker.userId, changeScore)
          roomPaijiu.addUserSocre(other.userId, -changeScore)
          addUserSocreForGold(banker.userId, changeScore)
          addUserSocreForGold(other.userId, -changeScore)
          other.winState = LOSE

          logger.info("庄家赢得钱: " + changeScore)



        } else if (result < 0) {
          other.winState = WIN
          winUsers = winUsers.+:(other)
        }else{
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
      val score2 = getGroupScore(playerInfo.group2)
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
      addUserSocreForGold(banker.userId, -loseScore)
      addUserSocreForGold(playerInfo.userId, loseScore)
    }
  }

  /**
    * 结算
    */
   def computeForGold(): Unit = {
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
        if (bankerScore1 >= otherScore1) result += 1
        if (bankerScore1 < otherScore1) result -= 1
        if (bankerScore2 >= otherScore2) result += 1
        if (bankerScore2 < otherScore2) result -= 1
        resultSet = resultSet.+(result)
        //庄家赢
        if (result > 0) {
          val changeScore = other.getBetScore(bankerScore2 >= mix8Score)
          banker.addScore(roomPaijiu,changeScore)
          other.addScore(roomPaijiu,-changeScore)
          roomPaijiu.bankerScore += changeScore
          roomPaijiu.addUserSocre(banker.userId, changeScore)
          roomPaijiu.addUserSocre(other.userId, -changeScore)
          addUserSocreForGold(banker.userId, changeScore)
          addUserSocreForGold(other.userId, -changeScore)
          other.winState = LOSE

          logger.info("庄家赢得钱: " + changeScore)



        } else if (result < 0) {
          other.winState = WIN
          winUsers = winUsers.+:(other)
        }else{
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
      val score2 = getGroupScore(playerInfo.group2)
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
      addUserSocreForGold(banker.userId, -loseScore)
      addUserSocreForGold(playerInfo.userId, loseScore)
    }
  }




  /**
    * 排序
    *
    * @param playerInfo1
    * @param playerInfo2
    * @return
    */
  def compareByScore(playerInfo1: PlayerCardInfoPaijiu, playerInfo2: PlayerCardInfoPaijiu): Boolean = {
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


  def addUserSocreForGold(userId: Long, score: Double) {
    val s = this.roomPaijiu.userScoresForGold.get(userId)
    this.roomPaijiu.userScoresForGold.put(userId, s + score)
  }
}
