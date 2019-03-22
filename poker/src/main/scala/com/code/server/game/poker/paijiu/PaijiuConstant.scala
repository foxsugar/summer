package com.code.server.game.poker.paijiu

/**
  * Created by sunxianping on 2017/7/24.
  */
trait PaijiuConstant {
  val ALL_CARD_NUM = 32

  val STATE_START = 0//开始
  val STATE_FIGHT_FOR_BANKER = 1//抢庄
  val STATE_BANKER_SET_SCORE = 2//庄家选分
  val STATE_BET = 3//下注
  val START_CRAP = 4//摇色子
  val STATE_OPEN = 5//开牌
  val STATE_BANKER_BREAK = 6//切庄


  //杂8的名字  大于等于杂8输两道
  val MIX_8 = "mixeight"
  val SKY_8 = "skyeight"

  val WIN:Int = 1
  val LOSE: Int = -1
  val DRAW:Int = 0


  val MODE_GUIZI = 1
  val MODE_ZHADAN = 2
  val MODE_TIANJIU = 3
  val MODE_DIJIU = 4

  val CARDSCORE = Map(1->1)

}
