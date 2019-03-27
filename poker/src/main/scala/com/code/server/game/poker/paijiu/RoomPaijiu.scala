package com.code.server.game.poker.paijiu

import com.code.server.constant.game.{IGameConstant, RoomStatistics}
import com.code.server.constant.response._
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.poker.service.PokerGoldRoom
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager
import com.code.server.util.timer.GameTimer
import com.code.server.util.{IdWorker, SpringUtil}
import org.springframework.beans.BeanUtils

import scala.beans.BeanProperty
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2017/7/24.
  */
class RoomPaijiu extends PokerGoldRoom {

  @BeanProperty
  var cards: List[Int] = List()
  @BeanProperty
  var lastGameCards: List[Int] = List()

  //庄家设置的分
  var bankerScore: Int = 0
  var bankerInitScore: Int = 0

  var isTest: Boolean = true

  var testUserId: Long = 0

  //机器人类型 0:没有 1:占位 2:玩牌
  var robotType:Int = 0
  //机器人数量
  var robotNum:Int = 0
  //机器人赢得数量
  var robotWinner:Int = 0
  //是否重开
  var isReOpen:Boolean = false

  //机器人列表
  var robotList:List[Long] = List()
  //上庄列表
  var bankerList:List[Long] = List()
  var bankerScoreMap:Map[Long,Int] = Map()

  //赢得索引
  var winnerIndex:ListBuffer[GamePaijiuResult] = ListBuffer()
  //三门获胜场次
  var winnerCountMap:Map[Int,Int] = Map()




  //  override protected def getGameInstance: Game = gameType match {
  //    case "11" => new GamePaijiuEndless
  //    case "12"=> new GamePaijiu2Cards
  //    case "13"=>new GamePaijiu2CardsEndless
  //    case _ => new GamePaijiu
  //  }

  override def startGame(): Unit = {
    //do nothing
    if (this.curGameNumber > 1) {
      MsgSender.sendMsg2Player(new ResponseVo("gameService", "gamePaijiuBegin", "ok"), this.getUsers)
      //开始游戏
      val game = getGameInstance
      this.game = game
      game.startGame(users, this)

      //游戏开始 代建房 去除定时解散
      if (!isOpen && !this.isCreaterJoin) GameTimer.removeNode(prepareRoomTimerNode)

      //扣钱
      if (!isOpen && isCreaterJoin) spendMoney()
      this.isInGame = true
      this.isOpen = true
      pushScoreChange()
    }
  }

  def isAceRoomOver():Boolean = {
    return false;
  }

  override def startGameByClient(userId: Long): Int = {
    //玩家是房主
    if (this.users.get(0) != userId) return ErrorCode.ROOM_START_NOT_CREATEUSER

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
    notifyCludGameStart()


    //游戏开始 代建房 去除定时解散
    if (!isOpen && !this.isCreaterJoin) GameTimer.removeNode(prepareRoomTimerNode)

    //扣钱
    if (!isOpen && isCreaterJoin) spendMoney()
    this.isInGame = true
    this.isOpen = true
    pushScoreChange()
    0
  }

  override protected def roomAddUser(userId: Long): Unit = {
    this.users.add(userId)
    this.userStatus.put(userId, 0)
    this.userScores.put(userId, 0D)
    this.roomStatisticsMap.put(userId, new RoomStatistics(userId))
    this.canStartUserId = users.get(0)
    //代开房
    if (!isCreaterJoin ||isClubRoom) this.bankerId = users.get(0)
    //如果是机器人
    if(RedisManager.getUserRedisService.getUserBean(userId).getRobot == 1) {
      this.robotList = this.robotList.+:(userId)
    }
    addUser2RoomRedis(userId)
  }


  override def roomRemoveUser(userId: Long): Unit = {
    this.users.remove(userId)
    this.userStatus.remove(userId)
    this.userScores.remove(userId)
    this.roomStatisticsMap.remove(userId)
    //如果在上庄列表里 删掉
    this.bankerList = this.bankerList.filter(_!=userId)
    this.bankerScoreMap = this.bankerScoreMap.filter(_!=userId)
    this.robotList = this.robotList.filter(_!=userId)
    removeUserRoomRedis(userId)
  }

  override def toVo(userId: Long): IfaceRoomVo = {
    val roomVo = new RoomPaijiuVo
    BeanUtils.copyProperties(super.toVo(userId), roomVo)
    roomVo.setBankerInitScore(this.bankerInitScore)
    roomVo.setBankerScore(this.bankerScore)
    roomVo.setBankerId(this.bankerId)
    roomVo.setWinnerIndex(this.winnerIndex.toList.asJava)
    roomVo.setWinnerCountMap(this.winnerCountMap.asJava)
    roomVo

  }

  /**
    * 解散房间
    */
  override protected def dissolutionRoom(): Unit = {
    //庄家初始分 再减掉
    import java.time.LocalDateTime

    import com.code.server.constant.response.{GameOfResult, ResponseVo}
    import com.code.server.game.room.kafka.MsgSender
    import com.code.server.game.room.service.RoomManager
    RoomManager.removeRoom(this.roomId)
    // 结果类
    val userOfResultList = getUserOfResult

    //代开房 并且游戏未开始
    if (!isCreaterJoin && !this.isInGame && (this.curGameNumber == 1)) {
      drawBack()
      GameTimer.removeNode(this.prepareRoomTimerNode)
    }
    this.isInGame = false

    // 存储返回
    val gameOfResult = new GameOfResult

    gameOfResult.setUserList(userOfResultList)
    gameOfResult.setEndTime(LocalDateTime.now.toString)
    MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users)

    //庄家初始分 再减掉
    this.addUserSocre(this.getBankerId, -this.bankerInitScore)

    //战绩
    genRoomRecord()
  }


}


object RoomPaijiu extends Room {
  def createRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int,clubId:String,clubRoomModel:String,isAA:Boolean,
                robotType:Int, robotNum:Int, robotWinner:Int, isReOpen:Boolean, otherMode:Int, personNum:Int): Int = {
    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    val roomPaijiu = new RoomPaijiu
    roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId(serverConfig.getServerId)))
    roomPaijiu.setRoomType(roomType)
    roomPaijiu.setGameType(gameType)
    roomPaijiu.setGameNumber(gameNumber)
    roomPaijiu.setBankerId(userId)
    roomPaijiu.setCreateUser(userId)
    roomPaijiu.setPersonNumber(personNum)
    roomPaijiu.setClubId(clubId)
    roomPaijiu.setClubRoomModel(clubRoomModel)
    roomPaijiu.setAA(isAA)

    roomPaijiu.robotType = robotType
    roomPaijiu.robotNum = robotNum
    roomPaijiu.robotWinner = robotWinner
    roomPaijiu.isReOpen = isReOpen
    roomPaijiu.otherMode = otherMode
    roomPaijiu.setRobotRoom(robotType!=0)




    roomPaijiu.init(gameNumber, 1)
    val code = roomPaijiu.joinRoom(userId, true)
    if (code != 0) return code

    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId, 0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoom", roomPaijiu.toVo(userId)), userId)
    0
  }

  def createRoomNotInRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int, isCreaterJoin: Boolean,clubId:String,clubRoomModel:String): Int = {
    RoomPaijiuByNotInRoom.createRoomNotInRoom(userId, roomType, gameType, gameNumber, isCreaterJoin,clubId, clubRoomModel)
  }

  /*def createRoomNotInRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int,isCreaterJoin:Boolean): Int = {
    val roomPaijiu = new RoomPaijiu
    roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId()))
    roomPaijiu.setRoomType(roomType)
    roomPaijiu.setGameType(gameType)
    roomPaijiu.setGameNumber(gameNumber)
    roomPaijiu.setBankerId(0l)
    roomPaijiu.setCreateUser(userId)
    roomPaijiu.setPersonNumber(4)
    roomPaijiu.setCreaterJoin(isCreaterJoin);
    roomPaijiu.init(gameNumber, 1)

    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId,0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoomNotInRoom", roomPaijiu.toVo(userId)), userId)
    0
}*/
}
