package com.code.server.game.poker.service

import com.code.server.constant.response.ErrorCode
import com.code.server.game.poker.cow.RoomCow
import com.code.server.game.poker.doudizhu.{RoomDouDiZhu, RoomDouDiZhuGold}
import com.code.server.game.poker.guess.RoomGuessCar
import com.code.server.game.poker.hitgoldflower.RoomHitGoldFlower
import com.code.server.game.poker.paijiu.{RoomGoldPaijiu, RoomPaijiu}
import com.code.server.game.poker.pullmice.RoomPullMice
import com.code.server.game.poker.tuitongzi.RoomTuiTongZi
import com.code.server.game.poker.zhaguzi.RoomZhaGuZi
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
        val personNum = params.path("personNum").asInt(3)
        val jiaoScore = params.path("jiaoScoreMax").asInt(3)

        val shuanglong = params.path("shuanglong").asInt(0
        )
        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)
        val showChat = params.path("showChat").asBoolean(false)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText

        return RoomDouDiZhu.createRoom(userId, gameNumber, multiple, gameType, roomType,isAA,isJoin,showChat,personNum,jiaoScore,shuanglong,clubId,clubRoomModel)

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
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText

        return RoomHitGoldFlower.createHitGoldFlowerRoom(userId, gameNumber,personNumber,cricleNumber,multiple,caiFen,menPai,gameType, roomType,isAA,isJoin,clubId,clubRoomModel)

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
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText

        return RoomCow.createCowRoom(userId, gameNumber,personNumber,multiple,gameType, roomType,isAA,isJoin,clubId,clubRoomModel);

      case "startCowGameByClient" =>
        val roomId = params.get("roomId").asText()
        return RoomCow.startGameByClient(userId,roomId);

      case "startTTZGameByClient" =>
        System.out.println("++++++++++---------------PokerRoomService+startTTZGameByClient")
        val roomId = params.get("roomId").asText()
        val room = RoomManager.getRoom(roomId)
        if (room == null) return ErrorCode.CAN_NOT_NO_ROOM
        return room.startGameByClient(userId)

      case "startPullMiceGameByClient" =>
        val roomId = params.get("roomId").asText()
        val room = RoomManager.getRoom(roomId)
        if (room == null) return ErrorCode.CAN_NOT_NO_ROOM
        return room.startGameByClient(userId)

      case "createPaijiuRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isAA = params.path("isAA").asBoolean(false)
        return RoomPaijiu.createRoom(userId,roomType, gameType,gameNumber,clubId,clubRoomModel,isAA)

      case "createPaijiuGoldRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isGold = params.path("isGold").asInt()
        val goldType = params.path("goldType").asInt()
        return RoomGoldPaijiu.createGoldRoom(userId,roomType, gameType,gameNumber,isGold,goldType)

      case "createTTZRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val personNumber = params.path("personNumber").asInt()
        val isJoin = params.path("isJoin").asBoolean(false)
        val multiple = params.path("multiple").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        return RoomTuiTongZi.createRoom(userId,roomType, gameType,gameNumber, personNumber, isJoin, multiple,clubId,clubRoomModel)

      case "createZGZRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val personNumber = params.path("personNumber").asInt()
        val isJoin = params.path("isJoin").asBoolean(false)
        val multiple = params.path("multiple").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isShowCard = params.path("showCard").asText
        return RoomZhaGuZi.createRoom(userId,roomType, gameType,gameNumber, personNumber, isJoin, multiple,clubId,clubRoomModel)

      case "createPullMiceRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val personNumber = params.path("personNumber").asInt()
        val isJoin = params.path("isJoin").asBoolean(false)
        val multiple = params.path("multiple").asInt()
        val hasWubuFeng = params.path("hasWubuFeng").asBoolean(false)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        return RoomPullMice.createRoom(userId,roomType, gameType,gameNumber, personNumber, isJoin, multiple, hasWubuFeng,clubId,clubRoomModel)

      case "createPaijiuRoomNotInRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val isCreaterJoin = params.path("isCreaterJoin").asBoolean()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        return RoomPaijiu.createRoomNotInRoom(userId,roomType, gameType,gameNumber,isCreaterJoin,clubId,clubRoomModel)
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

      case "getAllGoldPaijiuRoom"=>
        return RoomGoldPaijiu.getAllRoom(userId);

      case _ =>
        return -1
    }


  }
}
