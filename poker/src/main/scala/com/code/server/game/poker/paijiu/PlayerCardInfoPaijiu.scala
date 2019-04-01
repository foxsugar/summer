package com.code.server.game.poker.paijiu

import com.code.server.constant.response.{IfacePlayerInfoVo, PlayerCardInfoPaijiuVo}
import com.code.server.game.room.IfacePlayerInfo
import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2017/7/24.
  */
class PlayerCardInfoPaijiu extends IfacePlayerInfo with PaijiuConstant {

  var userId: Long = _
  //牌
  var cards: List[Int] = List()
  //下注
  var bet: Bet = _
  //开牌
  var group1: String = _
  var group2: String = _
  //赢
  var winState: Int = DRAW
  //分数
  var score: Double = 0

  //是否选择坐庄
  var isFightForBanker:Boolean = false

  //是否选择过抢庄
  var isHasFightForBanker:Boolean = false

  var robot:Boolean = false


  /**
    * 得到押注的分
    *
    * @param isWinTwo
    * @return
    */
  def getBetScore(isWinTwo: Boolean): Int = {
    if (bet == null) return 0
    var result = bet.one
    if (isWinTwo) result += bet.two
    result
  }

  /**
    * 加分数
    * @param room
    * @param add
    */
  def addScore(room:RoomPaijiu,add:Int): Unit ={
    this.score += add
    //设置最大分数
    val max = room.getRoomStatisticsMap.get(userId).maxScore
    if(add > max){
      room.getRoomStatisticsMap.get(userId).maxScore = add
    }

  }

  override def toVo: IfacePlayerInfoVo = {
    val playerCardInfoPaijiuVo = new PlayerCardInfoPaijiuVo()

    if (this.bet != null) {
      playerCardInfoPaijiuVo.bet1 = this.bet.one
      playerCardInfoPaijiuVo.bet2 = this.bet.two
      playerCardInfoPaijiuVo.bet3 = this.bet.three
    }
    if(this.group1!=null) playerCardInfoPaijiuVo.isOpenCard = true


    playerCardInfoPaijiuVo.group1 = this.group1
    playerCardInfoPaijiuVo.group2 = this.group2
    playerCardInfoPaijiuVo.cards = this.cards.asJava
    playerCardInfoPaijiuVo.userId = this.userId
    playerCardInfoPaijiuVo.winState = this.winState
    playerCardInfoPaijiuVo.score = this.score
    playerCardInfoPaijiuVo.isFightForBanker = this.isFightForBanker
    playerCardInfoPaijiuVo.isHasFightForBanker = this.isHasFightForBanker

    playerCardInfoPaijiuVo
  }


  def getBetNum(): Int ={
    if(bet != null) {
      return bet.one + bet.two + bet.three
    }
    0
  }


  override def toVo(watchUser: Long): IfacePlayerInfoVo = {
    if (watchUser == this.userId) return this.toVo

    val playerCardInfoPaijiuVo = new PlayerCardInfoPaijiuVo()
    if (this.bet != null) {
      playerCardInfoPaijiuVo.bet1 = this.bet.one
      playerCardInfoPaijiuVo.bet2 = this.bet.two
      playerCardInfoPaijiuVo.bet3 = this.bet.three
    }
    if(this.group1!=null) playerCardInfoPaijiuVo.isOpenCard = true

    playerCardInfoPaijiuVo.userId = this.userId
    playerCardInfoPaijiuVo.winState = this.winState
    playerCardInfoPaijiuVo.score = this.score
    playerCardInfoPaijiuVo.isFightForBanker = this.isFightForBanker
    playerCardInfoPaijiuVo.isHasFightForBanker = this.isHasFightForBanker

    playerCardInfoPaijiuVo
  }

  def setWinState(winOrLost:Int): Unit ={
    winState = winOrLost
  }

  def getScore(): Double ={
     score
  }

}


class Bet(o: Int, t: Int ,th :Int, ind:Int) {
  val one: Int = o
  val two: Int = t
  val three : Int = th
  val index: Int = ind

}