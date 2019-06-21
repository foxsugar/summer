package com.code.server.game.poker.paijiu

import com.code.server.constant.game.RoomStatistics
import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager
import com.code.server.util.timer.GameTimer
import com.code.server.util.{IdWorker, SpringUtil}

import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2019-06-17.
  */
class RoomTuitongziGold extends RoomPaijiu{




  override def roomRemoveUser(userId: Long): Unit = {
    this.users.remove(userId)
    this.userStatus.remove(userId)
    this.userScores.remove(userId)
    this.roomStatisticsMap.remove(userId)
    //如果在上庄列表里 删掉
    //如果现在是庄家,把钱退给他

    if (this.bankerId == userId) {
      RedisManager.getUserRedisService.addUserGold(userId, this.bankerScore)
      //      this.lastBankerInitScore = bankerInitScore
      this.bankerId = 0
      this.bankerInitScore = 0
      this.bankerScore = 0
    }
    if (bankerScoreMap.contains(userId)) {
      RedisManager.getUserRedisService.addUserGold(userId, bankerScoreMap(userId))
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
    this.userScores.put(userId, RedisManager.getUserRedisService.getUserGold(userId))
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


  /**
    * 100paijiu 不花钱
    *
    * @return
    */
  override def getNeedMoney(): Int = {
   return 0

  }

  override def spendMoney(): Unit = {
  }


  override protected def isCanJoinCheckMoney(userId: Long): Boolean = { //todo 检验金币
    true
  }

  /**
    * 最小金币
    *
    * @return
    */
  override protected def getOutGold(): Int = {
    return 0
  }

  override def startGame(): Unit = {
    //do nothing
//    if (this.curGameNumber > 1) {
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
//    }
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
    if (RedisManager.getUserRedisService.getUserGold(userId) < score) {
      return ErrorCode.CRAP_ALREADY_BANKER
    }
    //上庄先扣钱
    RedisManager.getUserRedisService.addUserMoney(userId, -score)
    this.bankerList = this.bankerList.+:(userId)
    this.bankerScoreMap = this.bankerScoreMap.+(userId -> score)

    //更新庄家
    //    updateBanker()

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "someOneTobeBanker", Map("userId" -> userId, "score" -> score).asJava), this.users)
    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "tuitongziTobeBanker", 0), userId)
    0
  }

}




object RoomTuitongziGold extends Room {
  def createRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int, clubId: String, clubRoomModel: String, clubMode: Int, isAA: Boolean,
                 robotType: Int, robotNum: Int, robotWinner: Int, isReOpen: Boolean, otherMode: Int, personNum: Int): Int = {
    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    val roomPaijiu = new RoomTuitongziGold
    roomPaijiu.setRoomId(Room.getRoomIdStr(Room.genRoomId(serverConfig.getServerId)))
    roomPaijiu.setRoomType(roomType)
    roomPaijiu.setGameType(gameType)
    roomPaijiu.setGameNumber(gameNumber)
    roomPaijiu.setBankerId(userId)
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


    roomPaijiu.init(gameNumber, 1)
    val code = roomPaijiu.joinRoom(userId, true)
    if (code != 0) return code

    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId, 0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoom", roomPaijiu.toVo(userId)), userId)
    0
  }



  def createRoom_(userId: Long, roomType: String, gameType: String, gameNumber: Int, clubId: String, clubRoomModel: String, clubMode: Int, isAA: Boolean,
                 robotType: Int, robotNum: Int, robotWinner: Int, isReOpen: Boolean, otherMode: Int, personNum: Int,bankerInitScore:Int): RoomTuitongziGold = {
    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    val roomPaijiu = new RoomTuitongziGold
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


    roomPaijiu.bankerScore = bankerInitScore
    roomPaijiu.bankerInitScore = bankerInitScore
    roomPaijiu.lastBankerInitScore  = bankerInitScore


    roomPaijiu.init(gameNumber, 1)
//    val code = roomPaijiu.joinRoom(userId, true)
//    if (code != 0) return code

//    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId, 0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuRoom", roomPaijiu.toVo(userId)), userId)
    roomPaijiu
  }
}
