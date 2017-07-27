package com.code.server.game.poker.paijiu

import com.code.server.constant.response.{IfacePlayerInfoVo, PlayerCardInfoPaijiuVo}
import com.code.server.game.room.IfacePlayerInfo

/**
  * Created by sunxianping on 2017/7/24.
  */
class PlayerCardInfoPaijiu extends IfacePlayerInfo with PaijiuConstant {

  var userId: Long = _
  //牌
  val cards: List[Int] = List()
  //下注
  var bet: Bet = _
  //开牌
  var group1: String = _
  var group2: String = _
  //赢
  var winState: Int = draw
  //分数
  var score: Double = 0


  /**
    * 得到押注的分
    *
    * @param isWinTwo
    * @return
    */
  def getBetScore(isWinTwo: Boolean): Int = {
    if (bet == null) 0
    var result = bet.one
    if (isWinTwo) result += bet.two
    result
  }

  override def toVo: IfacePlayerInfoVo = {
    new PlayerCardInfoPaijiuVo
  }

  override def toVo(watchUser: Long): IfacePlayerInfoVo = {
    new PlayerCardInfoPaijiuVo
  }
}


class Bet(o: Int, t: Int) {
  val one = o
  val two = t

}