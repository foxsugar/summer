package com.code.server.game.poker.service

import com.code.server.constant.response.{ReconnectResp, ResponseVo}
import com.code.server.game.room.MsgSender
import com.code.server.game.room.service.RoomManager

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
      reconnectResp.setRoom(room.toVo)
    }
    val vo = new ResponseVo("userService", "reconnection", reconnectResp)
    MsgSender.sendMsg2Player(vo, userId)
    0
  }
}
