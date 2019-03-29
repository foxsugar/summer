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


  val MODE_100 = 1//百人
  val MODE_2CARD = 2//两张牌
  val MODE_BET_3 = 3//三道
  val MODE_WINNER_PAY = 4//大赢家付

  val MODE_GUIZI = 10
  val MODE_ZHADAN = 11
  val MODE_TIANJIU = 12
  val MODE_DIJIU = 13


  val CARDSCORE = Map(
    1->12,
    2->12,
    3->2,
    4->2,
    5->8,
    6->8,
    7->4,
    8->4,
    9->10,
    10->10,
    11->6,
    12->6,
    13->4,
    14->4,
    15->11,
    16->11,
    17->10,
    18->10,
    19->7,
    20->7,
    21->6,
    22->6,
    23->9,
    24->9,
    25->8,
    26->8,
    27->7,
    28->7,
    29->5,
    30->5,
    31->6,
    32->3
  )

}
