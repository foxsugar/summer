package com.code.server.game.poker.service

import com.code.server.game.poker.doudizhu.{GameDouDiZhu, RoomDouDiZhu}
import com.fasterxml.jackson.databind.JsonNode

/**
  * Created by sunxianping on 2017/6/7.
  */
object PokerRoomService {
  def dispatch(userId:Long, method:String, params:JsonNode):Int = {

    method match {
      case "createRoom" =>
        val gameNumber = params.get("gameNumber").asInt()
        val multiple = params.get("maxMultiple").asInt()
        val gameType = params.get("gameType").asText("0")
        RoomDouDiZhu.createRoom(userId, gameNumber, multiple,gameType);
    }



  }
}
