package com.code.server.game.poker.paijiu

/**
  * 项目名称：${project_name}   
  * 类名称：${type_name}   
  * 类描述：   
  * 创建人：Clark  
  * 创建时间：${date} ${time}   
  * 修改人：Clark  
  * 修改时间：${date} ${time}   
  * 修改备注：   
  *
  * @version 1.0    
  */
class GamePaijiuEndless3Bet extends GamePaijiuEndless{




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
    if (banker.score <= 0 || banker.score >= 1000) {
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
    val sky8Score = getGroupScoreByName(SKY_8)
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
          if(bankerScore2 > sky8Score) {//第三道
            banker.addScore(this.roomPaijiu, other.bet.three)
            other.addScore(this.roomPaijiu, -other.bet.three)
            roomPaijiu.addUserSocre(banker.userId, other.bet.three)
            roomPaijiu.addUserSocre(other.userId, -other.bet.three)
            roomPaijiu.bankerScore += other.bet.three
          }
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

      if(score2 > sky8Score) {//第三道
        banker.addScore(this.roomPaijiu, -playerInfo.bet.three)
        playerInfo.addScore(this.roomPaijiu, playerInfo.bet.three)
        roomPaijiu.addUserSocre(banker.userId, -playerInfo.bet.three)
        roomPaijiu.addUserSocre(playerInfo.userId, playerInfo.bet.three)
        roomPaijiu.bankerScore -= playerInfo.bet.three
      }

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
    * 比较输赢并设置分数
    *
    * @param banker
    * @param other
    */
  override  def compareAndSetScore(banker: PlayerCardInfoPaijiu, other: PlayerCardInfoPaijiu): Int = {
    val mix8Score = getGroupScoreByName(MIX_8)
    val sky8Score = getGroupScoreByName(SKY_8)
    val bankerScore1 = getGroupScore(banker.group1)
    val bankerScore2 = getGroupScore(banker.group2)
    val otherScore1 = getGroupScore(other.group1)
    val otherScore2 = getGroupScore(other.group2)
    var result: Int = 0
    if (bankerScore1 >= otherScore1) result += 1
    if (bankerScore1 < otherScore1) result -= 1
    if (bankerScore2 >= otherScore2) result += 1
    if (bankerScore2 < otherScore2) result -= 1
    //庄家赢
    if (result > 0) {
      val changeScore = other.getBetScore(bankerScore2 >= mix8Score)
      banker.addScore(this.roomPaijiu, changeScore)
      other.addScore(this.roomPaijiu, -changeScore)
      roomPaijiu.addUserSocre(banker.userId, changeScore)
      roomPaijiu.addUserSocre(other.userId, -changeScore)
      if(bankerScore2 > sky8Score) {//第三道
        banker.addScore(this.roomPaijiu, other.bet.three)
        other.addScore(this.roomPaijiu, -other.bet.three)
        roomPaijiu.addUserSocre(banker.userId, other.bet.three)
        roomPaijiu.addUserSocre(other.userId, -other.bet.three)
      }
      other.winState = LOSE
    } else if (result < 0) {
      //闲家赢
      val changeScore = other.getBetScore(otherScore2 >= mix8Score)
        banker.addScore(this.roomPaijiu, -changeScore)
        other.addScore(this.roomPaijiu, changeScore)
        roomPaijiu.addUserSocre(banker.userId, -changeScore)
        roomPaijiu.addUserSocre(other.userId, changeScore)
        if(otherScore2 > sky8Score) {//第三道
          banker.addScore(this.roomPaijiu, other.bet.three)
          other.addScore(this.roomPaijiu, -other.bet.three)
          roomPaijiu.addUserSocre(banker.userId, other.bet.three)
          roomPaijiu.addUserSocre(other.userId, -other.bet.three)
      }
      other.winState = WIN
    }
    result
  }


}
