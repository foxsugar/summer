package com.code.server.game.poker.service

import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.poker.cow.RoomCow
import com.code.server.game.poker.doudizhu.RoomDouDiZhu
import com.code.server.game.poker.guess.RoomGuessCar
import com.code.server.game.poker.hitgoldflower.RoomHitGoldFlower
import com.code.server.game.poker.paijiu.{RoomGoldPaijiu, RoomPaijiu, RoomPaijiuAce}
import com.code.server.game.poker.playseven.RoomPlaySeven
import com.code.server.game.poker.pullmice.RoomPullMice
import com.code.server.game.poker.tiandakeng.RoomTDK
import com.code.server.game.poker.tuitongzi.RoomTuiTongZi
import com.code.server.game.poker.xuanqiqi.RoomXuanQiQi
import com.code.server.game.poker.yuxiaxie.RoomYuxiaxie
import com.code.server.game.poker.zhaguzi.{RoomWzq, RoomYSZ, RoomZhaGuZi}
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.game.room.{Room, RoomExtendGold}
import com.code.server.util.SpringUtil
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

        val shuanglong = params.path("shuanglong").asInt(0)
        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)
        val showChat = params.path("showChat").asBoolean(false)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val otherMode = params.path("otherMode").asInt(0)

        return RoomDouDiZhu.createRoom(userId, gameNumber, multiple, gameType, roomType, isAA, isJoin, showChat, personNum, jiaoScore, shuanglong, clubId, clubRoomModel,otherMode)

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
        val isRobot = params.path("isRobot").asBoolean(false)
        val time = params.path("time").asInt(10)
        val isJoinHalfWay = params.path("isJoinHalfWay").asBoolean(false)
        val wanjialiangpai = params.path("wanjialiangpai").asBoolean(false)
        val bipaijiabei = params.path("bipaijiabei").asBoolean(true)
        val otherMode = params.path("otherMode").asInt(0)



        return RoomHitGoldFlower.createHitGoldFlowerRoom(userId, gameNumber, personNumber, cricleNumber,
          multiple, caiFen, menPai, gameType, roomType, isAA, isJoin, clubId, clubRoomModel,
          isRobot, time, isJoinHalfWay,wanjialiangpai,bipaijiabei,otherMode)

      case "createXuanQiQiRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val personNumber = params.get("personNumber").asInt()
        val cricleNumber = params.get("cricleNumber").asInt()
        val multiple = params.get("multiple").asInt()
        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText

        return RoomXuanQiQi.createXuanQiQiRoom(userId, gameNumber, personNumber, cricleNumber, multiple, gameType, roomType, isAA, isJoin, clubId, clubRoomModel)

      case "createPlaySevenRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val personNumber = params.get("personNumber").asInt()
        val fengDing = params.get("fengDing").asInt()
        val kouDiJiaJi = params.path("kouDiJiaJi").asBoolean(true)
        val zhuangDanDaJiaBei = params.path("zhuangDanDaJiaBei").asBoolean(true)
        val multiple = params.get("multiple").asInt()
        val gameType = params.path("gameType").asText("0")
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText

        return RoomPlaySeven.createPlaySevenRoom(userId, gameNumber, fengDing, kouDiJiaJi, zhuangDanDaJiaBei, personNumber, multiple, gameType, roomType, isAA, isJoin, clubId, clubRoomModel)


      case "startGameByClient" =>
        val roomId = params.get("roomId").asText()
        return RoomHitGoldFlower.startGameByClient(userId, roomId);

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

        return RoomCow.createCowRoom(userId, gameNumber, personNumber, multiple, gameType, roomType, isAA, isJoin, clubId, clubRoomModel);

      case "startCowGameByClient" =>
        val roomId = params.get("roomId").asText()
        return RoomCow.startGameByClient(userId, roomId);

      case "startYSZGameByClient" =>
        val roomId = params.get("roomId").asText()
        return RoomYSZ.startGameByClient(userId, roomId);

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

      case "startTDKGameByClient"=>
        val roomId = params.get("roomId").asText()
        val room = RoomManager.getRoom(roomId)
        if (room == null) return ErrorCode.CAN_NOT_NO_ROOM
        return room.startGameByClient(userId)

      case "createPaijiuRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isAA = params.path("isAA").asBoolean(false)
        return RoomPaijiu.createRoom(userId, roomType, gameType, gameNumber, clubId, clubRoomModel, isAA)

      case "createPaijiuGoldRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isGold = params.path("isGold").asInt()
        val goldType = params.path("goldType").asInt()
        return RoomGoldPaijiu.createGoldRoom(userId, roomType, gameType, gameNumber, isGold, goldType)

      case "createPaijiuAceRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)
        return RoomPaijiuAce.createRoom(userId, roomType, gameType, gameNumber,isJoin,clubId,clubRoomModel, isAA)


      case "createTTZRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val personNumber = params.path("personNumber").asInt()
        val isJoin = params.path("isJoin").asBoolean(false)
        val multiple = params.path("multiple").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        //默认是2局
        var quan = params.path("quan").asInt(2)
        return RoomTuiTongZi.createRoom(userId, roomType, gameType, gameNumber, personNumber, isJoin, multiple, clubId, clubRoomModel, quan)

      case "createZGZRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val personNumber = params.path("personNumber").asInt()
        val isJoin = params.path("isJoin").asBoolean(false)
        val multiple = params.path("multiple").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isShowCard = params.path("showCard").asText
        return RoomZhaGuZi.createRoom(userId, roomType, gameType, gameNumber, personNumber, isJoin, multiple, clubId, clubRoomModel, isShowCard)


      case "createYSZRoom" =>
        val roomType = params.get("roomType").asText()
        val gameNumber = params.get("gameNumber").asInt()
        val personNumber = params.get("personNumber").asInt()
        val cricleNumber = params.get("cricleNumber").asInt()
        val multiple = params.get("multiple").asInt()
        val caiFen = params.get("caiFen").asInt()
        val menPai = params.get("menPai").asInt()

        val gameType = params.path("gameType").asText("0")
//        val isAA = params.path("isAA").asBoolean(false)
        //fix bug
        val isAA =params.path("isAA").asBoolean(false) || params.path("isAll").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(true)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val goldRoomType = params.path("goldRoomType").asInt(0)
        val goldRoomPermission = params.path("goldRoomPermission").asInt(0)
        return RoomYSZ.createYSZRoom(userId, gameNumber, personNumber, cricleNumber, multiple, caiFen, menPai, gameType, roomType, isAA, isJoin, clubId, clubRoomModel, goldRoomType, goldRoomPermission)

      case "createPullMiceRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val personNumber = params.path("personNumber").asInt()
        val isJoin = params.path("isJoin").asBoolean(false)
        val multiple = params.path("multiple").asInt()
        val hasWubuFeng = params.path("hasWubuFeng").asBoolean(false)
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        return RoomPullMice.createRoom(userId, roomType, gameType, gameNumber, personNumber, isJoin, multiple, hasWubuFeng, clubId, clubRoomModel)

      case "createPaijiuRoomNotInRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val gameNumber = params.path("gameNumber").asInt()
        val isCreaterJoin = params.path("isCreaterJoin").asBoolean()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        return RoomPaijiu.createRoomNotInRoom(userId, roomType, gameType, gameNumber, isCreaterJoin, clubId, clubRoomModel)

      case "createGuessRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val chip = params.path("chip").asInt()
        return RoomGuessCar.createRoom(userId, chip, gameType, roomType)

      case "guessCar" =>
        val roomId = params.get("roomId").asText()
        val redOrGreen = params.get("redOrGreen").asInt()
        val roomGuessCar = RoomManager.getRoom(roomId)
        if (roomGuessCar == null) {
          return ErrorCode.CAN_NOT_NO_ROOM
        }
        return roomGuessCar.asInstanceOf[RoomGuessCar].guessCar(userId, redOrGreen)

      case "createWZQRoom" =>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val personNumber = params.path("personNumber").asInt()
        val multiple = params.path("multiple").asInt()
        val gameNumber = params.path("gameNumber").asInt()
        //        if(multiple <=0 ) return ErrorCode.REQUEST_PARAM_ERROR
        return RoomWzq.createRoom(userId, roomType, gameType, multiple, personNumber, gameNumber)

      case "createTDKRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val personNumber = params.path("personNumber").asInt()
        val multiple = params.path("multiple").asInt()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(false)
        val showChat = params.path("showChat").asBoolean(false)
        val otherMode = params.path("otherMode").asInt(0)

        return RoomTDK.createRoom(userId, gameNumber,multiple,gameType, roomType,isAA,isJoin,showChat,personNumber,clubId,clubRoomModel,otherMode)

      case "createYXXRoom"=>
        val roomType = params.path("roomType").asText()
        val gameType = params.path("gameType").asText()
        val personNumber = params.path("personNumber").asInt()
        val multiple = params.path("multiple").asInt()
        val gameNumber = params.path("gameNumber").asInt()
        val clubId = params.path("clubId").asText
        val clubRoomModel = params.path("clubRoomModel").asText
        val isAA = params.path("isAA").asBoolean(false)
        val isJoin = params.path("isJoin").asBoolean(false)
        val showChat = params.path("showChat").asBoolean(false)
        val otherMode = params.path("otherMode").asInt(0)
        val danya = params.path("danya").asInt()
        val chuanlian = params.path("chuanlian").asInt()
        val baozi = params.path("baozi").asInt()
        val nuo = params.path("nuo").asInt()
        return RoomYuxiaxie.createRoom(userId, gameNumber,multiple,gameType, roomType,isAA,isJoin,showChat,personNumber,
          clubId,clubRoomModel,otherMode,danya, chuanlian, baozi, nuo)

        //鱼虾蟹 色子记录 todo 对协议
      case "getYXXDiceHistory"=>
        val roomId = params.get("roomId").asText()
        val roomYXX = RoomManager.getRoom(roomId)
        if (roomYXX == null) {
          return ErrorCode.CAN_NOT_NO_ROOM
        }
        return roomYXX.asInstanceOf[RoomYuxiaxie].getYXXDiceHistory(userId)

      case "setBankerByClient"=>
        val roomId = params.get("roomId").asText()
        val bankerId = params.get("bankerId").asLong()
        val roomYXX = RoomManager.getRoom(roomId)
        if (roomYXX == null) {
          return ErrorCode.CAN_NOT_NO_ROOM
        }
        return roomYXX.asInstanceOf[RoomYuxiaxie].setBankerByClient(userId, bankerId)

        //鱼虾蟹 下注记录
      case "getYXXBetHistory"=>
        val roomId = params.get("roomId").asText()
        val roomYXX = RoomManager.getRoom(roomId)
        if (roomYXX == null) {
          return ErrorCode.CAN_NOT_NO_ROOM
        }
        return roomYXX.asInstanceOf[RoomYuxiaxie].getYXXBetHistory(userId)
      case "getAllRoom" =>
        return RoomGuessCar.getAllRoom(userId);

      case "getAllGoldPaijiuRoom" =>
        return RoomGoldPaijiu.getAllRoom(userId);

      case "joinGoldRoom" =>

        val roomType: String = params.get("roomType").asText
        val gameType: String = params.get("gameType").asText
        val goldRoomType = params.path("goldRoomType").asInt()

        joinGoldRoom(userId, roomType, gameType, goldRoomType)


      case "getGoldRooms" =>

        val gameType: String = params.get("gameType").asText
//        val goldRoomType = params.path("goldRoomType").asInt(0)

        val result = RoomExtendGold.getGoldRoomsVo(gameType)
        MsgSender.sendMsg2Player("pokerRoomService", "getGoldRooms", result, userId)
        0


      case _ =>
        return -1
    }


  }


  def joinGoldRoom(userId: Long, roomType: String, gameType: String, goldRoomType: Int): Int = {
    val rooms = RoomManager.getInstance().getNotFullRoom(gameType, goldRoomType)
    var room: Room = null
    var isAdd = false
    if (rooms.size() > 0) {
      room = rooms.get(0)
    }
    if (room == null) {
      isAdd = true
      room = new PokerGoldRoom()
      room = room.getDefaultGoldRoomInstance(userId, roomType, gameType, goldRoomType)

    }
    val rtn: Int = room.joinRoom(userId, true)

    if (rtn != 0) {
      return rtn
    } else {
      if (isAdd) {
        val serverId: Int = SpringUtil.getBean(classOf[ServerConfig]).getServerId
        RoomManager.getInstance().addNotFullGoldRoom(room)
        RoomManager.addRoom(room.getRoomId, "" + serverId, room)
      }
      MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "joinGoldRoom", room.toVo(userId)), userId)
//      room.getReady(userId)
    }
    0
  }


}
