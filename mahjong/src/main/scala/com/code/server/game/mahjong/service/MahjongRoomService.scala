package com.code.server.game.mahjong.service

import com.code.server.constant.game.IGameConstant
import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.game.mahjong.config.ServerConfig
import com.code.server.game.mahjong.logic.RoomFactory
import com.code.server.game.mahjong.logic.RoomInfo
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager
import com.code.server.util.SpringUtil
import com.code.server.util.timer.GameTimer
import com.code.server.util.timer.ITimeHandler
import com.code.server.util.timer.TimerNode
import com.fasterxml.jackson.databind.JsonNode

/**
  * Created by sunxianping on 2017/6/5.
  */
object MahjongRoomService {
  def dispatch(userId: Long, method: String, paramsjSONObject: JsonNode): Int = {
    var code: Int = 0
    method match {
      case "createRoomByUser" => {
        val modeTotal: String = paramsjSONObject.get("modeTotal").asText
        val mode: String = paramsjSONObject.get("mode").asText
        val multiple: Int = paramsjSONObject.get("multiple").asInt
        val gameNumber: Int = paramsjSONObject.get("gameNumber").asInt
        val personNumber: Int = paramsjSONObject.get("personNumber").asInt
        val gameType: String = paramsjSONObject.get("gameType").asText
        code = createRoomByUser(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType)

      }
      case "createRoomByEachUser" => {
        val modeTotal: String = paramsjSONObject.get("modeTotal").asText
        val mode: String = paramsjSONObject.get("mode").asText
        val multiple: Int = paramsjSONObject.get("multiple").asInt
        val gameNumber: Int = paramsjSONObject.get("gameNumber").asInt
        val personNumber: Int = paramsjSONObject.get("personNumber").asInt
        val gameType: String = paramsjSONObject.get("gameType").asText
        code = createRoomByEachUser(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType)

      }
      case "createRoomButNotInRoom" => {
        val modeTotal: String = paramsjSONObject.get("modeTotal").asText
        val mode: String = paramsjSONObject.get("mode").asText
        val multiple: Int = paramsjSONObject.get("multiple").asInt
        val gameNumber: Int = paramsjSONObject.get("gameNumber").asInt
        val personNumber: Int = paramsjSONObject.get("personNumber").asInt
        val gameType: String = paramsjSONObject.get("gameType").asText
        code = createRoomButNotInRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType)

      }
    }
    return code
  }

  private def isCanCreate(modeTotal: String, mode: String, multiple: String): Boolean = {
    if (modeTotal == "1") {
      if (mode != "5" && mode != "6") {
        return false
      }
      if (multiple != "1" && multiple != "2" && multiple != "5") {
        return false
      }
    }
    else if (modeTotal == "2") {
      if (mode != "1" && mode != "2" && mode != "3" && mode != "4") {
        return false
      }
      if (multiple != "1" && multiple != "2" && multiple != "5") {
        return false
      }
    }
    else if (modeTotal == "3" || modeTotal == "15") {
      if (multiple != "1" && multiple != "2" && multiple != "5") {
        return false
      }
    }
    else if (modeTotal == "4") {
      if (mode != "303") {
        return false
      }
    }
    else if (modeTotal == "5") {
      if (multiple != "1" && multiple != "2" && multiple != "5") {
        return false
      }
    }
    else if (modeTotal == "6") {
      if (mode != "0") {
        return false
      }
    }
    else

    /**
      * 256封顶、128封顶、杠呲宝、未上听包三家、杠上开花、三七夹	二进制取,1是0否
      */ if (modeTotal == "10") {
      if (multiple != "25" && multiple != "50" && multiple != "100" && multiple != "200") {
        return false
      }
      if (mode.toInt > 63 || mode.toInt < 0) {
        return false
      }
    }
    else if (modeTotal == "11") {
      if (multiple != "25" && multiple != "50" && multiple != "100" && multiple != "200") {
        return false
      }
      if (mode.toInt > 63 || mode.toInt < 0) {
        return false
      }
    }
    else if (modeTotal == "12") {
      if (multiple != "1" && multiple != "2") {
        return false
      }
      if (mode.toInt != 12051314 && mode.toInt != 12051304 && mode.toInt != 12050314 && mode.toInt != 12050304 && mode.toInt != 12151314 && mode.toInt != 12151304 && mode.toInt != 12150314 && mode.toInt != 12150304) {
        return false
      }
    }
    else if (modeTotal == "13") {
      if (mode.toInt != 13051314 && mode.toInt != 13051304 && mode.toInt != 13050314 && mode.toInt != 13050304 && mode.toInt != 13151314 && mode.toInt != 13151304 && mode.toInt != 13150314 && mode.toInt != 13150304) {
        return false
      }
    }
    else if (modeTotal == "14") {
      if (multiple != "25" && multiple != "50" && multiple != "100" && multiple != "200") {
        return false
      }
      if (mode.toInt > 63 || mode.toInt < 0) {
        return false
      }
    }
    else if (modeTotal == "124") {
      if (mode.toInt != 124051314 && mode.toInt != 124051304 && mode.toInt != 124050314 && mode.toInt != 124050304 && mode.toInt != 124151314 && mode.toInt != 124151304 && mode.toInt != 124150314 && mode.toInt != 124150304) {
        return false
      }
    }
    else {
      return false
    }
    return true
  }

  def createRoom(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int, gameType: String, each: String, isJoin: Boolean): (Int, RoomInfo) = {
    val roomInfo: RoomInfo = RoomFactory.getRoomInstance(gameType)
    val roomId: String = Room.getRoomIdStr(Room.genRoomId)
    roomInfo.init(roomId, userId, modeTotal, mode, multiple, gameNumber, personNumber, userId, 0)
    roomInfo.setEach(each)
    var code = 0
    if (isJoin) {
      code = roomInfo.joinRoom(userId)
      if (code != 0) {
        return (code, null)
      }
    }
    val serverId: Int = SpringUtil.getBean(classOf[ServerConfig]).getServerId
    RoomManager.addRoom(roomInfo.getRoomId, "" + serverId, roomInfo)
    return (code, roomInfo)
  }

  def createRoomByEachUser(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int, gameType: String): Int = {
    if (!isCanCreate(modeTotal, mode, "" + multiple)) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    val (code, roomInfo) = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "1", true)
    if (code != 0) {
      return code
    }

    if (roomInfo == null) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomByEachUser", roomInfo.toJSONObject), userId)
    return 0
  }

  def createRoomByUser(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int, gameType: String): Int = {
    if (!isCanCreate(modeTotal, mode, "" + multiple)) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    val (code, roomInfo) = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "0", true)
    if (code != 0) {
      return code
    }
    MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomByUser", roomInfo.toJSONObject), userId)
    return 0
  }

  def createRoomButNotInRoom(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int, gameType: String): Int = {
    if (!isCanCreate(modeTotal, mode, "" + multiple)) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    if ("LQ" == gameType) {
      val money: Double = RedisManager.getUserRedisService.getUserMoney(userId)
      if (8 == gameNumber) {
        if (money < 30) {
          return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY
        }
        RedisManager.getUserRedisService.addUserMoney(userId, -30)
        RedisManager.addGold(userId, 3)
      }
      else if (16 == gameNumber) {
        if (money < 60) {
          return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY
        }
        RedisManager.getUserRedisService.addUserMoney(userId, -60)
        RedisManager.addGold(userId, 6)
      }
    }
    val (code, roomInfo) = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "2", false)
    if (code != 0) {
      return code
    }

    val roomId: String = roomInfo.getRoomId
    val start: Long = System.currentTimeMillis
    val node: TimerNode = new TimerNode(start, IGameConstant.ONE_HOUR, false, new ITimeHandler() {
      def fire() {
        try {
          val roomInfo: RoomInfo = RoomManager.getRoom(roomId).asInstanceOf[RoomInfo]
          if (roomInfo != null && !roomInfo.isInGame && roomInfo.getCurGameNumber == 1) {
            if ("LQ" == roomInfo.getGameType) {
              if (8 == roomInfo.getGameNumber) {
                RedisManager.getUserRedisService.addUserMoney(userId, 30)
                RedisManager.addGold(userId, -3)
              }
              else if (16 == roomInfo.getGameNumber) {
                RedisManager.getUserRedisService.addUserMoney(userId, 60)
                RedisManager.addGold(userId, -6)
              }
            }
            RoomManager.removeRoom(roomInfo.getRoomId)
          }
        }
        catch {
          case e: Exception => {
            e.printStackTrace()
          }
        }
      }
    })
    roomInfo.setTimerNode(node)
    GameTimer.addTimerNode(node)
    MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomButNotInRoom", roomInfo.toJSONObject), userId)
    return 0
  }

  //    public void onlinemethod(GamePlayer gamePlayer) {
  //
  //        Map<Integer, String> userRoom = GameManager.getInstance().getUserRoom();
  //
  //        String roomId = userRoom.get(gamePlayer.getUserId());//房间号
  //
  //        RoomInfo roomInfo = GameManager.getInstance().getRoom(roomId);
  //
  //        if (roomInfo != null) {
  //            List<Integer> roomUserId = roomInfo.getUsers();
  //
  //            JSONObject result = new JSONObject();
  //
  //            Map<String, Integer> mapresult = new HashMap<String, Integer>();
  //
  //            mapresult.put("id", gamePlayer.getUserId());
  //            mapresult.put("status", 1);
  //
  //            result.put("service", "gameService");
  //            result.put("method", "offline");
  //            result.put("params", new Gson().toJson(mapresult));
  //            result.put("code", "0");
  //
  //            serverContext.sendToOnlinePlayer(result, roomUserId);
  //        }
  //    }
}