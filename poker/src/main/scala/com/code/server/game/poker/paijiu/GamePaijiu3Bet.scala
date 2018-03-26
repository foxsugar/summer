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
class GamePaijiu3Bet extends GamePaijiu{

  /**
    * 比较输赢并设置分数
    *
    * @param banker
    * @param other
    */
  override protected def compareAndSetScore(banker: PlayerCardInfoPaijiu, other: PlayerCardInfoPaijiu): Int = {
    val mix8Score = getGroupScoreByName(MIX_8)
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
      val changeScore = other.getBetScore(bankerScore2 == mix8Score)
      banker.addScore(this.roomPaijiu, changeScore)
      other.addScore(this.roomPaijiu, -changeScore)
      roomPaijiu.addUserSocre(banker.userId, changeScore)
      roomPaijiu.addUserSocre(other.userId, -changeScore)
      if(bankerScore2 > mix8Score) {//第三道
        banker.addScore(this.roomPaijiu, banker.bet.three)
        other.addScore(this.roomPaijiu, -banker.bet.three)
        roomPaijiu.addUserSocre(banker.userId, banker.bet.three)
        roomPaijiu.addUserSocre(other.userId, -banker.bet.three)
      }
      other.winState = LOSE
    } else if (result < 0) {
      //闲家赢
      val changeScore = other.getBetScore(otherScore2 == mix8Score)
      banker.addScore(this.roomPaijiu, -changeScore)
      other.addScore(this.roomPaijiu, changeScore)
      roomPaijiu.addUserSocre(banker.userId, -changeScore)
      roomPaijiu.addUserSocre(other.userId, changeScore)
      if(otherScore2 > mix8Score) {//第三道
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
