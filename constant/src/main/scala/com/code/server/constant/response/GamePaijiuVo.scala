package com.code.server.constant.response

import java.util

/**
  * Created by sunxianping on 2017/7/28.
  */
class GamePaijiuVo extends IfaceGameVo{


  var playerCardInfos =  new util.HashMap[Long,IfacePlayerInfoVo]()



  var bankerId: Long = -1L

  //状态
  var state: Int = 0

  var lastCards:util.List[Int] = _
}
