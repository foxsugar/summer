package com.code.server.constant.response

import java.util

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Created by sunxianping on 2017/7/28.
  */
class GamePaijiuVo extends IfaceGameVo{


  var playerCardInfos =  new util.HashMap[Long,IfacePlayerInfoVo]()



  var bankerId: Long = -1L

  //状态
  var state: Int = 0
}
