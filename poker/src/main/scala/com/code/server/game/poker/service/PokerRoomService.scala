package com.code.server.game.poker.service

import com.code.server.constant.response.ErrorCode
import com.code.server.game.cow.RoomCow
import com.code.server.game.poker.doudizhu.{RoomDouDiZhu, RoomDouDiZhuGold}
import com.code.server.game.poker.guess.RoomGuessCar
import com.code.server.game.poker.hitgoldflower.RoomHitGoldFlower
import com.code.server.game.poker.paijiu.RoomPaijiu
import com.code.server.game.room.service.RoomManager
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
        val showChat = params.path("showChat").asBoolean(false)

        return RoomDouDiZhu.createRoom(userId, gameNumber, multiple, gameType, roomType,isAA,isJoin,showChat)

      case "createHitGoldFlowerRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val personNumber = params.get("personNumber").asInt()
        val cricleNumber = params.get("cricleNumber").asInt()
        val multiple = params.get("multiple").asInt()
        val caiFen = params.get("caiFen").asInt()
        val menPai = params.get("menPai").asInt()

        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)

        return RoomHitGoldFlower.createHitGoldFlowerRoom(userId, gameNumber,personNumber,cricleNumber,multiple,caiFen,menPai,gameType, roomType,isAA,isJoin)

      case "startGameByClient" =>
        val roomId = params.get("roomId").asText()
        return RoomHitGoldFlower.startGameByClient(userId,roomId);

      //牛牛
      case "createCowRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val personNumber = params.get("personNumber").asInt()
        val multiple = params.get("multiple").asInt()
        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)

        return RoomCow.createCowRoom(userId, gameNumber,personNumber,multiple,gameType, roomType,isAA,isJoin);

      case "startCowGameByClient" =>
        val roomId = params.get("roomId").asText()
        return RoomCow.startGameByClient(userId,roomId);



      case "createPaijiuRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        return RoomPaijiu.createRoom(userId,roomType, gameType,gameNumber)

      case "createPaijiuRoomNotInRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val isCreaterJoin = params.path("isCreaterJoin").asBoolean()
        return RoomPaijiu.createRoomNotInRoom(userId,roomType, gameType,gameNumber,isCreaterJoin)
      case "joinGoldRoom" =>
        val goldRoomType = params.get("goldRoomType").asDouble()
        val roomType = params.get("roomType").asText()
        val gameType = params.get("gameType").asText()
        return RoomDouDiZhuGold.joinGoldRoom(userId,goldRoomType,roomType,gameType);

      case "createGuessRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val chip = params.path("chip").asInt()
        return RoomGuessCar.createRoom(userId,chip, gameType, roomType)

      case "guessCar"=>
        val roomId = params.get("roomId").asText()
        val redOrGreen = params.get("redOrGreen").asInt()
        val roomGuessCar = RoomManager.getRoom(roomId)
        if(roomGuessCar == null) {
          return ErrorCode.CAN_NOT_NO_ROOM
        }
        return roomGuessCar.asInstanceOf[RoomGuessCar].guessCar(userId,redOrGreen)
      case "getAllRoom"=>
        return RoomGuessCar.getAllRoom(userId);
      case _ =>
        return -1
    }


  }
}
