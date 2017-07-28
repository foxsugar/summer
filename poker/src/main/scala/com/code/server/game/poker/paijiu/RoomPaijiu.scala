package com.code.server.game.poker.paijiu

import com.code.server.constant.game.IGameConstant
import com.code.server.constant.response.{ErrorCode}
import com.code.server.game.room.{Game, Room}

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
    //开始游戏
    super.startGame()
    0
  }



}

object RoomPaijiu {
  def createRoom(userId:Long,roomType:String, gameType:String, gameNumber:Int):Int = {
    val roomPaijiu = new RoomPaijiu
    roomPaijiu.createUser = userId
    roomPaijiu.roomType = roomType
    roomPaijiu.gameType = gameType
    roomPaijiu.gameNumber = gameNumber
    0
  }
}
