package com.code.server.game.poker.paijiu

import com.code.server.constant.game.IGameConstant
import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.game.room.{Game, Room}
import com.code.server.util.{JsonUtil, SpringUtil}

import scala.beans.BeanProperty

/**
  * Created by sunxianping on 2017/7/24.
  */
class RoomPaijiu extends Room {

  @BeanProperty
  var cards: List[Int] = List()
  @BeanProperty
  var lastGameCards: List[Int] = List()


  override protected def getGameInstance: Game = gameType match {
    case _ =>
      new GamePaijiu
  }

  override def startGame(): Unit = {
    //do nothing
  }


  override def startGameByClient(userId: Long): Int = {
    //玩家是房主
    if (this.createUser != userId) return ErrorCode.ROOM_START_NOT_CREATEUSER

    //第一局
    if (this.curGameNumber != 1) return ErrorCode.ROOM_START_CAN_NOT
    //房主已经准备
    if (userStatus.get(userId) != IGameConstant.STATUS_READY) return ErrorCode.ROOM_START_CAN_NOT

    //没准备的人
    userStatus.forEach((uid, status) => {
      if (status != IGameConstant.STATUS_READY) {
        roomRemoveUser(uid)
      }
    })

//    super.startGame()
    //通知其他人游戏已经开始
    MsgSender.sendMsg2Player(new ResponseVo("gameService", "gamePaijiuBegin", "ok"), this.getUsers)
    MsgSender.sendMsg2Player(new ResponseVo("roomService", "startGameByClient", 0), userId)



    //开始游戏
    val game = getGameInstance
    this.game = game
    game.startGame(users, this)

    //扣钱
    if (!isOpen && isCreaterJoin) spendMoney()
    this.isInGame = true
    //开始游戏
    this.isOpen = true
    pushScoreChange()
    0
  }



}

object RoomPaijiu extends Room{
  def createRoom(userId:Long,roomType:String, gameType:String, gameNumber:Int):Int = {
    val roomPaijiu = new RoomPaijiu
    roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId()))
    roomPaijiu.setRoomType(roomType)
    roomPaijiu.setGameType(gameType)
    roomPaijiu.setGameNumber(gameNumber)
    roomPaijiu.setBankerId(userId)
    roomPaijiu.setCreateUser(userId)
    roomPaijiu.setPersonNumber(4)

    val code = roomPaijiu.joinRoom(userId, true)
    if (code != 0) return code

    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoom", roomPaijiu.toVo(userId)), userId)
    0
  }

  def main(args: Array[String]): Unit = {
    val s = "{\"userId\":\"1\",\"roomType\":\"3\",\"gameType\":\"10\",\"gameNumber\":\"12\"}"
    val jsonNode = JsonUtil.readTree(s)
    val userId = jsonNode.path("userId").asLong()
    val roomType = jsonNode.path("roomType").asText()
    val gameType = jsonNode.path("gameType").asText()
    val gameNumber = jsonNode.path("gameNumber").asInt()
    createRoom(userId,roomType, gameType,gameNumber)


  }
}
