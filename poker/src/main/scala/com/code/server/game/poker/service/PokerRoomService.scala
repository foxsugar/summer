package com.code.server.game.poker.service

import com.code.server.game.poker.doudizhu.RoomDouDiZhu
import com.code.server.game.poker.paijiu.RoomPaijiu
import com.code.server.game.room.Room
import com.fasterxml.jackson.databind.JsonNode

/**
  * Created by sunxianping on 2017/6/7.
  */
object PokerRoomService {
  def dispatch(userId: Long, method: String, params: JsonNode): Int = {

    method match {
      case "createRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val multiple = params.get("maxMultiple").asInt()

        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)

        RoomDouDiZhu.createRoom(userId, gameNumber, multiple, gameType, roomType,isAA,isJoin);

      case "createPaijiuRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        RoomPaijiu.createRoom(roomType, gameType,gameNumber)
    }


  }
}
