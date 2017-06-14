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
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val multiple = params.get("maxMultiple").asInt()
        var gameType = "0"
        if(params.has("gameType")) {
          gameType = params.get("gameType").asText()
        }
        RoomDouDiZhu.createRoom(userId, gameNumber, multiple,gameType,roomType);
    }



  }
}
