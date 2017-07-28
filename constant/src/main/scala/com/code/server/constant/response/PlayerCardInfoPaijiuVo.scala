package com.code.server.constant.response
import scala.collection.JavaConverters._


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

  //开牌
  var group1: String = _
  var group2: String = _
  //赢
  var winState: Int = 0
  //分数
  var score: Double = 0

}
