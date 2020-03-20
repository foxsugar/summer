package com.code.server.game.mahjong.service

import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.game.mahjong.config.ServerConfig
import com.code.server.game.mahjong.logic.{RoomFactory, RoomInfo, RoomInfoExtendGold}
import com.code.server.game.room.{Room, RoomExtendGold}
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager
import com.code.server.util.timer.{GameTimer, TimerNode}
import com.code.server.util.{IdWorker, SpringUtil}
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
        val roomType = paramsjSONObject.get("roomType").asText
        val mustZimo = paramsjSONObject.path("mustZimo").asInt
        val isHasYipaoduoxiang = paramsjSONObject.path("yipaoduoxiang").asBoolean(false)
        val canChi = paramsjSONObject.path("canChi").asBoolean(false)
        val haveTing = paramsjSONObject.path("haveTing").asBoolean(false)
        val clubId = paramsjSONObject.path("clubId").asText
        val clubRoomModel = paramsjSONObject.path("clubRoomModel").asText
        val clubMode = paramsjSONObject.path("clubMode").asInt(0)
        val goldRoomType = paramsjSONObject.path("goldRoomType").asInt(0)
        val goldRoomPermission = paramsjSONObject.path("goldRoomPermission").asInt(0)
        val showChat = paramsjSONObject.path("showChat").asBoolean(true)
        val otherMode = paramsjSONObject.path("otherMode").asInt(0)

        code = createRoomByUser(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType,
          roomType, mustZimo, isHasYipaoduoxiang, canChi, haveTing,
          clubId, clubRoomModel, clubMode, goldRoomType, goldRoomPermission, showChat, otherMode)

      }
      case "createRoomByEachUser" => {
        val modeTotal: String = paramsjSONObject.get("modeTotal").asText
        val mode: String = paramsjSONObject.get("mode").asText
        val multiple: Int = paramsjSONObject.get("multiple").asInt
        val gameNumber: Int = paramsjSONObject.get("gameNumber").asInt
        val personNumber: Int = paramsjSONObject.get("personNumber").asInt
        val gameType: String = paramsjSONObject.get("gameType").asText
        val roomType = paramsjSONObject.get("roomType").asText()
        val mustZimo = paramsjSONObject.path("mustZimo").asInt
        val isHasYipaoduoxiang = paramsjSONObject.path("yipaoduoxiang").asBoolean(false)
        val canChi = paramsjSONObject.path("canChi").asBoolean(false)
        val haveTing = paramsjSONObject.path("haveTing").asBoolean(false)
        val clubId = paramsjSONObject.path("clubId").asText
        val clubRoomModel = paramsjSONObject.path("clubRoomModel").asText
        val clubMode = paramsjSONObject.path("clubMode").asInt(0)
        val goldRoomType = paramsjSONObject.path("goldRoomType").asInt(0)
        val goldRoomPermission = paramsjSONObject.path("goldRoomPermission").asInt(0)
        val showChat = paramsjSONObject.path("showChat").asBoolean(true)
        val otherMode = paramsjSONObject.path("otherMode").asInt(0)
        code = createRoomByEachUser(userId, modeTotal, mode, multiple, gameNumber,
          personNumber, gameType, roomType, mustZimo, isHasYipaoduoxiang, canChi, haveTing,
          clubId, clubRoomModel, clubMode, goldRoomType, goldRoomPermission, showChat, otherMode)

      }
      case "createRoomButNotInRoom" => {
        val modeTotal: String = paramsjSONObject.get("modeTotal").asText
        val mode: String = paramsjSONObject.get("mode").asText
        val multiple: Int = paramsjSONObject.get("multiple").asInt
        val gameNumber: Int = paramsjSONObject.get("gameNumber").asInt
        val personNumber: Int = paramsjSONObject.get("personNumber").asInt
        val gameType: String = paramsjSONObject.get("gameType").asText
        val roomType = paramsjSONObject.get("roomType").asText()
        val mustZimo: Int = paramsjSONObject.path("mustZimo").asInt
        val isHasYipaoduoxiang = paramsjSONObject.path("yipaoduoxiang").asBoolean(false)
        val canChi = paramsjSONObject.path("canChi").asBoolean(false)
        val haveTing = paramsjSONObject.path("haveTing").asBoolean(false)
        val clubId = paramsjSONObject.path("clubId").asText
        val clubRoomModel = paramsjSONObject.path("clubRoomModel").asText
        val clubMode = paramsjSONObject.path("clubMode").asInt(0)
        val goldRoomType = paramsjSONObject.path("goldRoomType").asInt(0)
        val goldRoomPermission = paramsjSONObject.path("goldRoomPermission").asInt(0)
        val showChat = paramsjSONObject.path("showChat").asBoolean(true)
        val otherMode = paramsjSONObject.path("otherMode").asInt(0)
        code = createRoomButNotInRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber,
          gameType, roomType, mustZimo, isHasYipaoduoxiang, canChi, haveTing,
          clubId, clubRoomModel, clubMode, goldRoomType, goldRoomPermission, showChat, otherMode)

      }

      case "joinGoldRoom" => {

        val roomType: String = paramsjSONObject.get("roomType").asText
        val gameType: String = paramsjSONObject.get("gameType").asText
        val goldRoomType = paramsjSONObject.path("goldRoomType").asInt()

        code = joinGoldRoom(userId, roomType, gameType, goldRoomType)

      }

      case "getGoldRooms" => {
        //        val roomType: String = paramsjSONObject.get("roomType").asText
        val gameType: String = paramsjSONObject.get("gameType").asText
        //        val goldRoomType = paramsjSONObject.path("goldRoomType").asInt(0)

        val result = RoomExtendGold.getGoldRoomsVo(gameType)
        MsgSender.sendMsg2Player("mahjongRoomService", "getGoldRooms", result, userId)
        code = 0
      }


      case "getRoomGameNum" => {
        val rooms = paramsjSONObject.path("rooms").asText()
        MsgSender.sendMsg2Player("mahjongRoomService", "getRoomGameNum", Room.getRoomNum(rooms), userId)
        0
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
      if (mode != "1" && mode != "2" && mode != "3" && mode != "4" && mode != "11" && mode != "12" && mode != "13" && mode != "14") {
        return false
      }
      if (multiple != "1" && multiple != "2" && multiple != "3" && multiple != "5") {
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
    else if (modeTotal == "20") {
      return true
    }
    else if (modeTotal == "30") {
      return true
    }
    else if (modeTotal == "31") {
      return true
    }
    else if (modeTotal == "100") {
      return true
    }
    else if (modeTotal == "33") {
      return true
    }
    else {
      return true
    }
    return true
  }

  def createRoom(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int, gameType: String,
                 each: String, isJoin: Boolean, roomType: String, mustZimo: Int, yipaoduoxiang: Boolean, canChi: Boolean, haveTing: Boolean,
                 clubId: String, clubRoomModel: String, clubMode: Int, goldRoomType: Int, goldRoomPermission: Int, showChat: Boolean, otherMode: Int): (Int, RoomInfo) = {
    val roomInfo: RoomInfo = RoomFactory.getRoomInstance(gameType)
    val serverId: Int = SpringUtil.getBean(classOf[ServerConfig]).getServerId
    val roomId: String = Room.getRoomIdStr(Room.genRoomId(serverId))
    roomInfo.setRoomType(roomType)
    roomInfo.setAA("1".equals(each))
    roomInfo.setEach(each)
    roomInfo.setClubId(clubId)
    roomInfo.setClubRoomModel(clubRoomModel)
    roomInfo.setCreaterJoin(isJoin)
    roomInfo.setYipaoduoxiang(yipaoduoxiang)
    roomInfo.setCanChi(canChi)
    roomInfo.setHaveTing(haveTing)
    roomInfo.setGoldRoomType(goldRoomType)
    roomInfo.setGoldRoomPermission(goldRoomPermission)
    roomInfo.setShowChat(showChat)
    roomInfo.setOtherMode(otherMode)
    roomInfo.setClubMode(clubMode)

    if (Room.isHasMode(2, otherMode)) {
      roomInfo.setRobotRoom(true)
    }
    roomInfo.init(roomId, userId, modeTotal, mode, multiple, gameNumber, personNumber, userId, 0, mustZimo)
    if (goldRoomPermission != 0) {
      roomInfo.setMultiple(goldRoomType)
    }
    var code = 0
    if (isJoin) {
      //todo 选座位
      code = roomInfo.joinRoom(userId, true)
      if (code != 0) {
        return (code, null)
      }
    }

    RoomManager.addRoom(roomInfo.getRoomId, "" + serverId, roomInfo)
    val idWorker = new IdWorker(serverId, 0)
    roomInfo.setUuid(idWorker.nextId())
    return (code, roomInfo)
  }

  /**
    * 四人建房
    *
    * @param userId
    * @param modeTotal
    * @param mode
    * @param multiple
    * @param gameNumber
    * @param personNumber
    * @param gameType
    * @param roomType
    * @param mustZimo
    * @param yipaoduoxiang
    * @param canChi
    * @param haveTing
    * @return
    */
  def createRoomByEachUser(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int,
                           gameType: String, roomType: String, mustZimo: Int, yipaoduoxiang: Boolean, canChi: Boolean, haveTing: Boolean,
                           clubId: String, clubRoomModel: String, clubMode: Int, goldRoomType: Int, goldRoomPermission: Int, showChat: Boolean, otherMode: Int): Int = {
    if (!isCanCreate(modeTotal, mode, "" + multiple)) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    val (code, roomInfo) = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType, "1",
      true, roomType, mustZimo, yipaoduoxiang, canChi, haveTing,
      clubId, clubRoomModel, clubMode, goldRoomType, goldRoomPermission, showChat, otherMode)
    if (code != 0) {
      return code
    }

    if (roomInfo == null) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomByEachUser", roomInfo.toJSONObject), userId)
    return 0
  }


  /**
    * 普通房
    *
    * @param userId
    * @param modeTotal
    * @param mode
    * @param multiple
    * @param gameNumber
    * @param personNumber
    * @param gameType
    * @param roomType
    * @param mustZimo
    * @param yipaoduoxiang
    * @param canChi
    * @param haveTing
    * @return
    */
  def createRoomByUser(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int,
                       gameType: String, roomType: String, mustZimo: Int, yipaoduoxiang: Boolean, canChi: Boolean,
                       haveTing: Boolean, clubId: String, clubRoomModel: String, clubMode: Int, goldRoomType: Int, goldRoomPermission: Int, showChat: Boolean, otherMode: Int): Int = {
    if (!isCanCreate(modeTotal, mode, "" + multiple)) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    val (code, roomInfo) = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber, gameType,
      "0", true, roomType, mustZimo, yipaoduoxiang, canChi, haveTing,
      clubId, clubRoomModel, clubMode, goldRoomType, goldRoomPermission, showChat, otherMode)
    if (code != 0) {
      return code
    }
    MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomByUser", roomInfo.toJSONObject), userId)
    return 0
  }

  /**
    * 代建房
    *
    * @param userId
    * @param modeTotal
    * @param mode
    * @param multiple
    * @param gameNumber
    * @param personNumber
    * @param gameType
    * @param roomType
    * @param mustZimo
    * @param yipaoduoxiang
    * @param canChi
    * @param haveTing
    * @return
    */
  def createRoomButNotInRoom(userId: Long, modeTotal: String, mode: String, multiple: Int, gameNumber: Int, personNumber: Int,
                             gameType: String, roomType: String, mustZimo: Int, yipaoduoxiang: Boolean, canChi: Boolean, haveTing: Boolean,
                             clubId: String, clubRoomModel: String, clubMode: Int, goldRoomType: Int, goldRoomPermission: Int, showChat: Boolean, otherMode: Int): Int = {
    if (!isCanCreate(modeTotal, mode, "" + multiple)) {
      return ErrorCode.CANNOT_CREATE_ROOM_PARAMETER_IS_ERROR
    }
    //    val money: Double = RedisManager.getUserRedisService.getUserMoney(userId)
    //    val need = RoomInfo.getCreateMoney(gameType, gameNumber)
    //    if(money < need) {
    //      return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY
    //    }
    //    RedisManager.getUserRedisService.addUserMoney(userId, -need)
    //    if ("LQ" == gameType) {
    //      RedisManager.addGold(userId, need/10)
    //    }
    val (code, roomInfo) = createRoom(userId, modeTotal, mode, multiple, gameNumber, personNumber,
      gameType, "2", false, roomType, mustZimo, yipaoduoxiang, canChi, haveTing,
      clubId, clubRoomModel, clubMode, goldRoomType, goldRoomPermission, showChat, otherMode)
    if (code != 0) {
      return code
    }


    if (RedisManager.getUserRedisService.getUserMoney(userId) < roomInfo.getCreateNeedMoney) {
      RoomManager.removeRoom(roomInfo.getRoomId)
      return ErrorCode.CANNOT_JOIN_ROOM_NO_MONEY
    }
    //给代建房 开房者 扣钱
    roomInfo.spendMoney()


    val roomId = roomInfo.getRoomId
    val start: Long = System.currentTimeMillis
    val node: TimerNode = roomInfo.getDissolutionRoomTimerNode
    roomInfo.setPrepareRoomTimerNode(node)
    GameTimer.addTimerNode(node)
    MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "createRoomButNotInRoom", roomInfo.toJSONObject), userId)
    return 0
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
      room = new RoomInfoExtendGold()
      //获得一个默认房间
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
      MsgSender.sendMsg2Player(new ResponseVo("mahjongRoomService", "joinGoldRoom", room.asInstanceOf[RoomInfo].toJSONObject), userId)
    }
    return 0

  }

}