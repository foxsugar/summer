package com.code.server.game.poker.paijiu

import com.code.server.constant.game.IGameConstant
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

  override def spendMoney(): Unit = {

  }

  override def isRoomOver: Boolean = false


  override def addUserSocre(userId: Long, score: Double): Unit = {
    //百人牌九 加分时抽水
    if (this.isInstanceOf[RoomPaijiu100]) {
      if(score>0) {
        val game = this.game.asInstanceOf[GamePaijiuCrazy]
        val multiple = rebateData.get(IGameConstant.PAIJIU_BET).asInstanceOf[String].toDouble
        val s = score * (100 - multiple) / 100
        //返利
        val rs = score * rebateData.get(IGameConstant.PAIJIU_REBATE100).asInstanceOf[String].toDouble / 100
        RedisManager.getUserRedisService.addUserMoney(userId, s)
//        if(Room.isHasMode(MODE_2CARD,this.otherMode)){
//          sendCenterAddThreeRebate(userId, score,1)
//        }
        //发送返利
        sendCenterAddRebate(userId, rs)

      }else{
        RedisManager.getUserRedisService.addUserMoney(userId, score)
      }

//      var s = score
//      if(s<0) s = -s
//      if(s!=0) {
//        sendCenterAddThreeRebate(userId, s,1)
//      }
    }
  }


  override protected def isCanJoinCheckMoney(userId: Long): Boolean = {
    return true
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
//    getReady(userId)
    if (this.game != null) if (!this.game.getUsers.contains(userId)) {
      this.game.users.add(userId)
      val playerPaijiu = new PlayerCardInfoPaijiu
      playerPaijiu.userId = userId
      this.game.asInstanceOf[GamePaijiu].addUser(userId, playerPaijiu)
    }
    0
  }

  override def clearReadyStatus(isAddGameNum: Boolean): Unit = {
//    lastOperateTime = System.currentTimeMillis
    this.setGame(null)
    this.setInGame(false)
    import scala.collection.JavaConversions._
    for (entry <- this.userStatus.entrySet) {
      entry.setValue(IGameConstant.STATUS_JOIN)
    }
    if (isAddGameNum) this.curGameNumber += 1
  }

}
