package com.code.server.game.poker.paijiu

import com.code.server.constant.game.IGameConstant
import com.code.server.constant.response.{ErrorCode, IfaceRoomVo, ResponseVo, RoomPaijiuVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.game.room.{Game, Room}
import com.code.server.util.{IdWorker, SpringUtil}
import org.springframework.beans.BeanUtils

import scala.beans.BeanProperty

/**
  * Created by sunxianping on 2017/7/24.
  */
class RoomPaijiu extends Room {

  @BeanProperty
  var cards: List[Int] = List()
  @BeanProperty
  var lastGameCards: List[Int] = List()

  //庄家设置的分
  var bankerScore: Int = 0
  var bankerInitScore: Int = 0

  var isTest:Boolean = true

  var testUserId:Long = 0


  override protected def getGameInstance: Game = gameType match {
    case "11" => new GamePaijiuEndless
    case _ => new GamePaijiu
  }

  override def startGame(): Unit = {
    //do nothing
    if(this.curGameNumber>1) {
      MsgSender.sendMsg2Player(new ResponseVo("gameService", "gamePaijiuBegin", "ok"), this.getUsers)
      //开始游戏
      val game = getGameInstance
      this.game = game
      game.startGame(users, this)

      //扣钱
      if (!isOpen && isCreaterJoin) spendMoney()
      this.isInGame = true
      this.isOpen = true
      pushScoreChange()
    }
  }


  override def startGameByClient(userId: Long): Int = {
    //玩家是房主
    if (this.createUser != userId) return ErrorCode.ROOM_START_NOT_CREATEUSER

    //第一局
    if (this.curGameNumber != 1) return ErrorCode.ROOM_START_CAN_NOT
    //房主已经准备
    if (userStatus.get(userId) != IGameConstant.STATUS_READY) return ErrorCode.ROOM_START_CAN_NOT

    //准备的人数大于2
    val readyCount = userStatus.values().stream().filter(status => status == IGameConstant.STATUS_READY).count()
    if (readyCount < 2) return ErrorCode.READY_NUM_ERROR

    //设置persionnum
    this.setPersonNumber(userScores.size())

    //没准备的人
    var removeList = List[java.lang.Long]()
    userStatus.forEach((uid, status) => {
      if (status != IGameConstant.STATUS_READY) {
        removeList = removeList.+:(uid)
      }
    })
    for (removeId <- removeList) {
      roomRemoveUser(removeId)
    }

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
    this.isOpen = true
    pushScoreChange()
    0
  }

  override def toVo(userId: Long): IfaceRoomVo = {
    val roomVo:RoomPaijiuVo = new RoomPaijiuVo
    BeanUtils.copyProperties(super.toVo(userId), roomVo)
    roomVo.setBankerInitScore(this.bankerInitScore)
    roomVo.setBankerScore(this.bankerScore)
    roomVo.setBankerId(this.bankerId)
    roomVo

  }

  /**
    * 解散房间
    */
  override protected def dissolutionRoom(): Unit = {
    //庄家初始分 再减掉
    this.addUserSocre(this.getBankerId, -this.bankerInitScore)
    super.dissolutionRoom()
  }


}



object RoomPaijiu extends Room {
  def createRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int): Int = {
    val roomPaijiu = new RoomPaijiu
    roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId()))
    roomPaijiu.setRoomType(roomType)
    roomPaijiu.setGameType(gameType)
    roomPaijiu.setGameNumber(gameNumber)
    roomPaijiu.setBankerId(userId)
    roomPaijiu.setCreateUser(userId)
    roomPaijiu.setPersonNumber(4)
    roomPaijiu.init(gameNumber, 1)
    val code = roomPaijiu.joinRoom(userId, true)
    if (code != 0) return code

    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId,0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoom", roomPaijiu.toVo(userId)), userId)
    0
  }

}
