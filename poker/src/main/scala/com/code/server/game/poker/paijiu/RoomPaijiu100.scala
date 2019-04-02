package com.code.server.game.poker.paijiu

import com.code.server.redis.service.RedisManager

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


  override def addUserSocre(userId: Long, score: Double): Unit = {
    super.addUserSocre(userId, score)

    //返利
    if(score > 0) {

    }else{
      RedisManager.getUserRedisService.addUserMoney(userId, score)

    }
  }

}
