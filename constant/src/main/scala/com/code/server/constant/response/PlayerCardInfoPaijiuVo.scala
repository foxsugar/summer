package com.code.server.constant.response


/**
  * Created by sunxianping on 2017/7/28.
  */
class PlayerCardInfoPaijiuVo extends IfacePlayerInfoVo{
  var userId: Long = _
  //牌
  var cards:java.util.List[Int] = _
  //下注
  var bet1:Int = _
  var bet2:Int = _
  var bet3:Int = _
  var index:Int = _


  //开牌
  var group1: String = _
  var group2: String = _
  //赢
  var winState: Int = 0
  //分数
  var score: Double = 0

  var isOpenCard :Boolean = false

  var isFightForBanker:Boolean = false

  //是否选择过抢庄
  var isHasFightForBanker:Boolean = false

}
