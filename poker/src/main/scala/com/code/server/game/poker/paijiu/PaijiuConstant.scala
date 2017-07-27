package com.code.server.game.poker.paijiu

/**
  * Created by sunxianping on 2017/7/24.
  */
trait PaijiuConstant {
  val ALL_CARD_NUM = 32

  val STATE_START = 0
  val STATE_BET = 1
  val STATE_OPEN = 2

  //杂8的名字  大于等于杂8输两道
  val MIX_8 = "mixeight"

  val win = 1
  val lose = -1
  val draw = 0

}
