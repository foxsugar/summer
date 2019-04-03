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


  /**
    * 生成房间战绩
    */
  override def genRoomRecord(): Unit = {
    //do nothing
  }

  /**
    * 加入房间
    * @param userId
    * @param isJoin
    *     */
  override def joinRoom(userId: Long, isJoin: Boolean): Int = {
    val rtn = super.joinRoom(userId, isJoin)
    if (rtn != 0) return rtn
    getReady(userId)
    if (this.game != null) if (!this.game.getUsers.contains(userId)) {
      this.game.users.add(userId)
      val playerPaijiu = new PlayerCardInfoPaijiu
      playerPaijiu.userId = userId
      this.game.asInstanceOf[GamePaijiu].addUser(userId, playerPaijiu)
    }
    0
  }

}
