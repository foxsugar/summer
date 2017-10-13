package com.code.server.game.poker.service

import com.code.server.constant.response.{ReconnectResp, ResponseVo}
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager

/**
  * Created by sunxianping on 2017/6/7.
  */
object ReconnService {

  def dispatch(userId: Long, method: String, roomId: String): Int = {
    method match {
      case "reconnection" => reconnection(userId, roomId);

    }
  }

  def reconnection(userId: Long, roomId: String): Int = {
    val reconnectResp = new ReconnectResp
    reconnectResp.setExist(false)
    val room = RoomManager.getRoom(roomId)
    if (room != null) {
      reconnectResp.setExist(true)
      reconnectResp.setRoom(room.toVo(userId))
      if(room.getGame!=null){
        reconnectResp.setGame(room.getGame.toVo())
      }
      //在线状态
      room.getUsers.forEach(user=>reconnectResp.getOfflineStatus.put(user,RedisManager.getUserRedisService.getGateId(user)!= null))
    }
    val vo = new ResponseVo("reconnService", "reconnection", reconnectResp)
    MsgSender.sendMsg2Player(vo, userId)
    0
  }
}
