package com.code.server.game.poker.paijiu

/**
  * Created by sunxianping on 2019-03-26.
  */
class RoomPaijiu100 extends RoomPaijiuCrazy{


  /**
    * 100paijiu 不花钱
    * @return
    */
  override def getNeedMoney(): Int = {
    return 0
  }

  override def isRoomOver: Boolean = false

}
