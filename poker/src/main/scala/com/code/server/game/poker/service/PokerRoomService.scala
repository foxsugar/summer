package com.code.server.game.poker.service

import com.code.server.game.poker.doudizhu.{RoomDouDiZhu, RoomDouDiZhuGold}
import com.code.server.game.poker.hitgoldflower.RoomHitGoldFlower
import com.code.server.game.poker.paijiu.RoomPaijiu
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

        return RoomDouDiZhu.createRoom(userId, gameNumber, multiple, gameType, roomType,isAA,isJoin)

      case "createHitGoldFlowerRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val personNumber = params.get("personNumber").asInt()
        val cricleNumber = params.get("cricleNumber").asInt()
        val multiple = params.get("maxMultiple").asInt()
        val caiFen = params.get("caiFen").asInt()
        val menPai = params.get("menPai").asInt()

        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)

        return RoomHitGoldFlower.createHitGoldFlowerRoom(userId, gameNumber,personNumber,cricleNumber,multiple,caiFen,menPai,gameType, roomType,isAA,isJoin)

      case "createPaijiuRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        return RoomPaijiu.createRoom(userId,roomType, gameType,gameNumber)

      case "joinGoldRoom" =>
        val goldRoomType = params.get("goldRoomType").asDouble()
        val roomType = params.get("roomType").asText()
        val gameType = params.get("gameType").asText()
        return RoomDouDiZhuGold.joinGoldRoom(userId,goldRoomType,roomType,gameType);

      case _ =>
        return -1
    }


  }
}
