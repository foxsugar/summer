package com.code.server.game.poker.paijiu

import java.util
import java.util.Random

import com.code.server.constant.game.{IGameConstant, RoomRecord, RoomStatistics, UserBean, UserRecord}
import com.code.server.constant.kafka.{IKafaTopic, IkafkaMsgId, KafkaMsgKey}
import com.code.server.constant.response.{ErrorCode, GameOfResult, IfaceRoomVo, ResponseVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.kafka.MsgProducer
import com.code.server.redis.service.RedisManager
import com.code.server.util.timer.GameTimer
import com.code.server.util.{IdWorker, SpringUtil}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
  * Created by sunxianping on 2019-03-25.
  */
class RoomPaijiuCrazy extends RoomPaijiu with PaijiuConstant {
   val logger:Logger = LoggerFactory.getLogger(RoomPaijiuCrazy.getClass)


  override def roomRemoveUser(userId: Long): Unit = {
    this.users.remove(userId)
    this.userStatus.remove(userId)
    this.userScores.remove(userId)
    this.roomStatisticsMap.remove(userId)
    //如果在上庄列表里 删掉
    //如果现在是庄家,把钱退给他

    if (this.bankerId == userId) {
      RedisManager.getUserRedisService.addUserMoney(userId, this.bankerScore)
//      this.lastBankerInitScore = bankerInitScore
      this.bankerId = 0
      this.bankerInitScore = 0
      this.bankerScore = 0
    }
    if (bankerScoreMap.contains(userId)) {
      RedisManager.getUserRedisService.addUserMoney(userId, bankerScoreMap(userId))
    }
    this.bankerList = this.bankerList.filter(_ != userId)
    this.bankerScoreMap = this.bankerScoreMap.filterKeys(_ != userId)
    this.robotList = this.robotList.filter(_ != userId)
    removeUserRoomRedis(userId)

    //重置房间密码状态
    if (this.users.size() == 0) {
      this.alreadySet = false
      this.usePass = false
      this.pass = 0
    }
  }


  override protected def roomAddUser(userId: Long): Unit = {
    this.users.add(userId)
    this.userStatus.put(userId, 0)
    this.userScores.put(userId, RedisManager.getUserRedisService.getUserMoney(userId))
    this.roomStatisticsMap.put(userId, new RoomStatistics(userId))
    this.canStartUserId = users.get(0)
    //代开房
    if (!isCreaterJoin || isClubRoom) this.bankerId = users.get(0)
    //如果是机器人
    if (RedisManager.getUserRedisService.getUserBean(userId).getVip == 1) {
      this.robotList = this.robotList.+:(userId)
    }
    addUser2RoomRedis(userId)
  }


  override def startGame(): Unit = {
    if(this.game != null) {
      return
    }
    //do nothing

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


  override def quitRoom(userId: Long): Int = {
    if (!this.users.contains(userId)) return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST
    if (isInGame) {
      //并且在游戏中下注
      val game = this.game.asInstanceOf[GamePaijiu]
      val player = game.playerCardInfos(userId)
      if (player != null) {
        if (this.bankerId == userId || player.bet != null)
          return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME
      }
    }


    //删除玩家房间映射关系
    roomRemoveUser(userId)
    if (game != null) {
      game.users.remove(userId)
    }
    noticeQuitRoom(userId)
    0
  }

  /**
    * 100paijiu 不花钱
    *
    * @return
    */
  override def getNeedMoney(): Int = {
    if (!isAA) {
      return this.rebateData.get(IGameConstant.PAIJIU_PAY_ONE).asInstanceOf[String].toDouble.toInt
    } else {
      return 2
      return this.rebateData.get(IGameConstant.PAIJIU_PAY_AA).asInstanceOf[String].toDouble.toInt
    }

  }


  override protected def isCanJoinCheckMoney(userId: Long): Boolean = { //代建房
    return RedisManager.getUserRedisService.getUserMoney(userId) >=  2*bankerInitScore
//    return RedisManager.getUserRedisService.getUserMoney(userId) >= createNeedMoney + 2*bankerInitScore
  }


  override protected def setResultOtherInfo(gameOfResult: GameOfResult): Unit = {

  }


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


    //庄家初始分 再减掉
    //todo 庄家的分如何处理
    //    this.addUserSocre(this.getBankerId, -this.bankerInitScore)
    if(this.bankerId != 0 && this.bankerScore>0) {
      RedisManager.getUserRedisService.addUserMoney(this.bankerId, bankerScore)
    }

    // 存储返回
    val gameOfResult = new GameOfResult

    gameOfResult.setUserList(userOfResultList)
    gameOfResult.setEndTime(LocalDateTime.now.toString)


    if (!this.isAA && !this.isInstanceOf[RoomPaijiu100]) {
      //找到大赢家
      val winner = this.getMaxScoreUser
      val money = this.rebateData.get(IGameConstant.PAIJIU_PAY_ONE).asInstanceOf[String].toDouble
      //付房费
      RedisManager.getUserRedisService.addUserMoney(winner, -money)
      gameOfResult.setOther(Map("isAA" -> this.isAA, "winnerId" -> winner, "cost" -> money).asJava)

    } else {
      gameOfResult.setOther(Map("isAA" -> this.isAA, "cost" -> this.getNeedMoney).asJava)

    }

    //返利
    if(!this.isInstanceOf[RoomPaijiu100]) {

      val rebate:Double = this.rebateData.get(IGameConstant.PAIJIU_REBATE4).asInstanceOf[String].toDouble
      for(userId <- this.users){
//        this.sendCenterAddRebate(userId, rebate)
        this.sendCenterAddThreeRebate(userId, rebate,0)
      }
    }


    MsgSender.sendMsg2Player(new ResponseVo("gameService", "askNoticeDissolutionResult", gameOfResult), users)


    //战绩
    genRoomRecord()

    if (this.isReOpen) {
      var personNum = this.personNumber
      if(this.personNumber<4){
        personNum = 4
      }
      RoomPaijiuCrazy.createRoom(this.createUser, this.getRoomType, this.getGameType, this.getGameNumber, this.getClubId, this.getClubRoomModel, this.getClubMode,
        this.isAA, this.robotType, this.robotNum, this.robotWinner, this.isReOpen, this.getOtherMode, personNum, this.bankerInitScore)
    }
  }


  override def spendMoney(): Unit = {

    createNeedMoney = 0

    //大赢家最后付钱
    if (isAA) {
      createNeedMoney = this.rebateData.get(IGameConstant.PAIJIU_PAY_AA).asInstanceOf[String].toDouble.toInt
      createNeedMoney = 2

      this.users.forEach((userId) => {
        RedisManager.getUserRedisService.addUserMoney(userId, -2)
      })
    }

    this.users.forEach(userId=>{
      sendCenterAddContribute(userId, 2)
    })
//    super.spendMoney()

  }

  override def addUserSocre(userId: Long, score: Double): Unit = {
    //    super.addUserSocre(userId, score)
    //百人牌九 加分时抽水
    if (!this.isInstanceOf[RoomPaijiu100]) {
//      if (score > 0) {
//        //返利
//        val rs = score * rebateData.get(IGameConstant.PAIJIU_REBATE4).asInstanceOf[String].toDouble /100
//        RedisManager.getUserRedisService.addUserMoney(userId, score-rs)
//        //发送返利
//        sendCenterAddRebate(userId, rs)
//      } else {

        RedisManager.getUserRedisService.addUserMoney(userId, score)
//      }
    }

  }


  override def pushScoreChange(): Unit = {
    var userScores = new util.HashMap[Long, Double]()
    for (userId <- users) {
      userScores.put(userId, RedisManager.getUserRedisService.getUserMoney(userId))
    }
    MsgSender.sendMsg2Player(new ResponseVo("gameService", "scoreChange", userScores), this.getUsers)
  }

  /**
    * 排队上庄
    *
    * @param userId
    * @param score
    * @return
    */
  def tobeBanker(userId: Long, score: Int): Int = {

    if (this.bankerScoreMap.contains(userId) || this.bankerId == userId) {
      return ErrorCode.CRAP_ALREADY_BANKER
    }
    if (score <= 0) {
      return ErrorCode.CRAP_ALREADY_BANKER
    }
    val money = RedisManager.getUserRedisService.getUserMoney(userId)
    if (money < score) {
      return ErrorCode.CRAP_ALREADY_BANKER
    }
    if(this.game != null) {
      val gameInfo = this.game.asInstanceOf[GamePaijiuCrazy]
      val playerInfoOp = gameInfo.playerCardInfos.get(userId)
      if(playerInfoOp.nonEmpty) {
        val playerInfo = playerInfoOp.get
        if(money < score + playerInfo.getBetNum()) {
          return ErrorCode.CRAP_ALREADY_BANKER
        }
      }

    }
    //上庄先扣钱
    RedisManager.getUserRedisService.addUserMoney(userId, -score)
    this.bankerList = this.bankerList:+ userId
    this.bankerScoreMap = this.bankerScoreMap.+(userId -> score)
    //更新庄家
    //    updateBanker()

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "someOneTobeBanker", Map("userId" -> userId, "score" -> score).asJava), this.users)
    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "paijiuTobeBanker", 0), userId)

    pushScoreChange()
    0
  }




  /**
    * 设置是否用密码
    *
    * @param userId
    * @param flag
    * @return
    */
  def paijiuSetPass(userId: Long, flag: Boolean): Int = {
    if (this.alreadySet) return ErrorCode.CANNOT_ALREADY_SET
    this.alreadySet = true
    this.usePass = flag
    if (flag) {
      this.pass = new Random().nextInt(9999)
    }

    val result = Map("flag" -> flag, "pass" -> this.pass)
    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "paijiuSetPass", result.asJava), userId)
    0
  }

  /**
    * 生成战绩
    */
  override def genRoomRecord(): Unit = {
    if (!isOpen) return
    val roomRecord = new RoomRecord
    roomRecord.setRoomId(this.roomId)
    roomRecord.setId(this.getUuid)
    roomRecord.setType(this.roomType)
    roomRecord.setTime(System.currentTimeMillis)
    roomRecord.setClubId(clubId)
    roomRecord.setClubRoomModel(clubRoomModel)
    roomRecord.setGameType(gameType)
    roomRecord.setCurGameNum(this.curGameNumber)
    roomRecord.setAllGameNum(this.gameNumber)
    roomRecord.setOpen(isOpen)
    this.users.forEach(userId => {
      val userRecord = new UserRecord
      userRecord.setScore(this.roomStatisticsMap(userId).score)
      userRecord.setUserId(userId)
      val userBean = RedisManager.getUserRedisService.getUserBean(userId)
      if (userBean != null) userRecord.setName(userBean.getUsername)
      roomRecord.getRecords.add(userRecord)
    })

    val kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_ROOM_RECORD)
    val msgProducer = SpringUtil.getBean(classOf[MsgProducer])
    msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord)
  }


  override def dissolutionRoom(userId: Long): Int = {
    this.isReOpen = false
    dissolutionRoom()
    MsgSender.sendMsg2Player(new ResponseVo("roomService", "dissolutionRoom", "ok"), userId)
    0
  }



  override def joinRoom(userId: Long, isJoin: Boolean): Int = {
//    if(this.robotType == 1 && this.robotNum == 4) {
//      val userBean = RedisManager.getUserRedisService.getUserBean(userId)
//      if(userBean != null && userBean.getVip != 1)  return ErrorCode.CANNOT_JOIN_ROOM_IS_FULL
//    }
    val rtn = super.joinRoom(userId, isJoin)
    if (rtn != 0) return rtn
    if (!this.isInstanceOf[RoomPaijiu100]) {
      getReady(userId)
    }
    0
  }
}

object RoomPaijiuCrazy extends Room with PaijiuConstant {

  def createRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int, clubId: String, clubRoomModel: String, clubMode: Int, isAA: Boolean,
                 robotType: Int, robotNum: Int, robotWinner: Int, isReOpen: Boolean, otherMode: Int, personNum: Int, bankerInitScore: Int): Int = {
    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    var roomPaijiu = new RoomPaijiuCrazy
    if (Room.isHasMode(MODE_100, otherMode)) {
      roomPaijiu = new RoomPaijiu100
    }
    roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId(serverConfig.getServerId)))
    roomPaijiu.setRoomType(roomType)
    roomPaijiu.setGameType(gameType)
    roomPaijiu.setGameNumber(gameNumber)
    //    roomPaijiu.setBankerId(userId)
    roomPaijiu.setCreateUser(userId)
    roomPaijiu.setPersonNumber(personNum)
    roomPaijiu.setClubId(clubId)
    roomPaijiu.setClubRoomModel(clubRoomModel)
    roomPaijiu.setClubMode(clubMode)
    roomPaijiu.setAA(isAA)

    roomPaijiu.robotType = robotType
    roomPaijiu.robotNum = robotNum
    roomPaijiu.robotWinner = robotWinner
    roomPaijiu.isReOpen = isReOpen
    roomPaijiu.otherMode = otherMode
    roomPaijiu.setRobotRoom(robotType != 0)

    //锅底
    roomPaijiu.bankerScore = bankerInitScore
    roomPaijiu.bankerInitScore = bankerInitScore
    roomPaijiu.lastBankerInitScore  = bankerInitScore

    roomPaijiu.rebateData = RedisManager.getConstantRedisService.getConstant
    roomPaijiu.init(gameNumber, 1)
//    val code = roomPaijiu.joinRoom(userId, false)
//    if (code != 0) return code

    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId, 0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuCrazyRoom", roomPaijiu.toVo(userId)), userId)
    0
  }


  /**
    * 获得房间
    *
    * @param userId
    * @param crazyType
    */
  def getCrazyRoom(userId: Long, crazyType: Int): Int = {
    var roomList = ListBuffer[IfaceRoomVo]()
    RoomManager.getInstance().getRooms
    for (room <- RoomManager.getInstance().getRooms.values()) {
      //四人
      if (crazyType == 0 && !room.isInstanceOf[RoomPaijiu100] && room.isInstanceOf[RoomPaijiuCrazy]) {
        roomList.append(room.toVo(0))
      }
      //百人
      if (crazyType == 1 && room.isInstanceOf[RoomPaijiu100]) {
        roomList.append(room.toVo(0))
      }
    }

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getCrazyRoom", roomList.asJava), userId)
    0
  }


  def getPaijiuPlayerNum(userId: Long): Int = {
    var map: java.util.HashMap[String, Int] = new java.util.HashMap()
    for (room <- RoomManager.getInstance().getRooms.values()) {
      if (room.isInstanceOf[RoomPaijiu]) {
        val paijiuRoom = room.asInstanceOf[RoomPaijiu]
        val count = map.getOrElse(paijiuRoom.getGameType, 0) + room.getUsers.size()
        map.put(paijiuRoom.getGameType, count)
      }

    }

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getPaijiuPlayerNum", map), userId)
    0
  }


}