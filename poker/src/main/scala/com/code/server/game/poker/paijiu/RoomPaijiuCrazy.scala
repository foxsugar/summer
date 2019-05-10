package com.code.server.game.poker.paijiu

import java.util

import com.code.server.constant.game.{IGameConstant, RoomStatistics}
import com.code.server.constant.response.{ErrorCode, IfaceRoomVo, ResponseVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager
import com.code.server.util.timer.GameTimer
import com.code.server.util.{IdWorker, SpringUtil}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
  * Created by sunxianping on 2019-03-25.
  */
class RoomPaijiuCrazy extends RoomPaijiu with PaijiuConstant {


  override def roomRemoveUser(userId: Long): Unit = {
    this.users.remove(userId)
    this.userStatus.remove(userId)
    this.userScores.remove(userId)
    this.roomStatisticsMap.remove(userId)
    //如果在上庄列表里 删掉
    //如果现在是庄家,把钱退给他
    if (this.bankerId != userId && bankerScoreMap.contains(userId)) {
      RedisManager.getUserRedisService.addUserMoney(userId, bankerScoreMap(userId))
      this.bankerId = 0
      this.bankerInitScore = 0
      this.bankerScore = 0
    }
    this.bankerList = this.bankerList.filter(_ != userId)
    this.bankerScoreMap = this.bankerScoreMap.filterKeys(_ != userId)
    this.robotList = this.robotList.filter(_ != userId)
    removeUserRoomRedis(userId)
  }



  override protected def roomAddUser(userId: Long): Unit = {
    this.users.add(userId)
    this.userStatus.put(userId, 0)
    this.userScores.put(userId, RedisManager.getUserRedisService.getUserMoney(userId))
    this.roomStatisticsMap.put(userId, new RoomStatistics(userId))
    this.canStartUserId = users.get(0)
    //代开房
    if (!isCreaterJoin ||isClubRoom) this.bankerId = users.get(0)
    //如果是机器人
    if(RedisManager.getUserRedisService.getUserBean(userId).getVip == 1) {
      this.robotList = this.robotList.+:(userId)
    }
    addUser2RoomRedis(userId)
  }


  override def startGame(): Unit = {
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
      if(player != null ) {
        if(this.bankerId == userId || player.bet != null)
        return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME
      }
    }


    //删除玩家房间映射关系
    roomRemoveUser(userId)
    if(game!= null) {
      game.users.remove(userId)
    }
//    //        GameManager.getInstance().getUserRoom().remove(userId);
//    if (this.createUser == userId) { //房主解散
//      val n = new Notice
//      n.setMessage("roomNum " + this.getRoomId + " :has destroy success!")
//      MsgSender.sendMsg2Player("roomService", "destroyRoom", n, this.getUsers)
//      //代开房 并且游戏未开始
//      if (!isCreaterJoin && this.curGameNumber == 1) dissolutionRoom()
//      val room_ = RoomManager.getRoom(this.roomId).asInstanceOf[Room]
//      if (room_ != null) RoomManager.removeRoom(this.roomId)
//    }
    noticeQuitRoom(userId)
    0
  }

  /**
    * 100paijiu 不花钱
    * @return
    */
  override def getNeedMoney(): Int = {
    if(Room.isHasMode(MODE_WINNER_PAY, this.otherMode) && !isAA){
      return this.rebateData.get(IGameConstant.PAIJIU_PAY_ONE).asInstanceOf[String].toDouble.toInt
    }else{
      return this.rebateData.get(IGameConstant.PAIJIU_PAY_AA).asInstanceOf[String].toDouble.toInt
    }

  }


  override def spendMoney(): Unit = {

    createNeedMoney = this.rebateData.get(IGameConstant.PAIJIU_PAY_AA).asInstanceOf[String].toDouble.toInt

    //大赢家最后付钱
    if (!isAA && Room.isHasMode(MODE_WINNER_PAY, this.otherMode)) {

    } else {
      super.spendMoney()
    }
  }

  override def addUserSocre(userId: Long, score: Double): Unit = {
    super.addUserSocre(userId, score)
    //百人牌九 加分时抽水
    if (!this.isInstanceOf[RoomPaijiu100]) {
      if(score>0) {


//      val game: GamePaijiuCrazy = this.game.asInstanceOf[GamePaijiuCrazy]
//      val multiple = rebateData.get(IGameConstant.PAIJIU_BET).asInstanceOf[String].toDouble
//      val s = score * (100 - multiple) / 100
      RedisManager.getUserRedisService.addUserMoney(userId, score)
      //返利
      val rs = score * rebateData.get(IGameConstant.PAIJIU_REBATE4).asInstanceOf[String].toDouble

      //发送返利
      sendCenterAddRebate(userId, rs)
      }else{

        RedisManager.getUserRedisService.addUserMoney(userId, score)
      }
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
      ErrorCode.CRAP_ALREADY_BANKER
    }
    if (score <= 0) {
      ErrorCode.CRAP_ALREADY_BANKER
    }
    if (RedisManager.getUserRedisService.getUserMoney(userId) < score) {
      ErrorCode.CRAP_ALREADY_BANKER
    }
    //上庄先扣钱
    RedisManager.getUserRedisService.addUserMoney(userId, -score)
    this.bankerList = this.bankerList.+:(userId)
    this.bankerScoreMap = this.bankerScoreMap.+(userId -> score)

    //更新庄家
    updateBanker()

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "someOneTobeBanker", Map("userId"->userId, "score"->score).asJava), this.users)
    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "paijiuTobeBanker", 0), userId)
    0
  }


  def getPaijiuBankerList(userId:Long):Int = {
    val result = Map("bankerList"->this.bankerList.asJava, "bankerScoreMap"->this.bankerScoreMap.asJava)
    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getPaijiuBankerList", result.asJava), userId)
    0
  }

  /**
    * 更新banker
    */
  def updateBanker(): Unit = {
    //现在没有banker
    if ((this.bankerId == 0 || this.bankerId == -1) && this.bankerList.nonEmpty) {
      //排队的第一个

      val userId = this.bankerList.head
      this.bankerId = userId
      this.canStartUserId = userId
      this.bankerScore = this.bankerScoreMap(userId)

      this.bankerInitScore = this.bankerScoreMap(userId)

      this.bankerList = this.bankerList.filter(_ != userId)
      this.bankerScoreMap = this.bankerScoreMap.filterKeys(_ != userId)

      //推送
      MsgSender.sendMsg2Player(new ResponseVo("gameService", "updatePaijiuBanker", Map("userId" -> userId,"score"->this.bankerScore).asJava), this.getUsers)
      //更新时间
      this.updateLastOperateTime()
      pushRemainTime(getRemainTime())

      //改局数
      this.curGameNumber = 1

      println("更新banker 更新后id为: " + userId)

    }
  }



  override def joinRoom(userId: Long, isJoin: Boolean): Int = {
    val rtn = super.joinRoom(userId, isJoin)
    if (rtn != 0) return rtn
    if(!this.isInstanceOf[RoomPaijiu100]) {
      getReady(userId)
    }
    0
  }


  override protected def isCanJoinCheckMoney(userId: Long): Boolean = {

    if(!this.isInstanceOf[GamePaijiu100]) {
      if(isAA) {
        RedisManager.getUserRedisService.getUserMoney(userId) >= bankerInitScore + getNeedMoney()
      }else{
        RedisManager.getUserRedisService.getUserMoney(userId) >= bankerInitScore
      }
    }else{
      super.isCanJoinCheckMoney(userId)
    }

  }

}


object RoomPaijiuCrazy extends Room with PaijiuConstant {

  def createRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int, clubId: String, clubRoomModel: String, clubMode: Int, isAA: Boolean,
                 robotType: Int, robotNum: Int, robotWinner: Int, isReOpen: Boolean, otherMode: Int, personNum: Int,bankerInitScore:Int): Int = {
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

    roomPaijiu.rebateData = RedisManager.getConstantRedisService.getConstant
    roomPaijiu.init(gameNumber, 1)
    val code = roomPaijiu.joinRoom(userId, false)
    if (code != 0) return code

    RoomManager.addRoom(roomPaijiu.getRoomId, "" + serverConfig.getServerId, roomPaijiu)
    val idword = new IdWorker(serverConfig.getServerId, 0)
    roomPaijiu.setUuid(idword.nextId())

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuCrazyRoom", roomPaijiu.toVo(userId)), userId)
    0
  }


  /**
    * 获得房间
    * @param userId
    * @param crazyType
    */
  def getCrazyRoom(userId:Long,crazyType:Int): Int ={
    var roomList = ListBuffer[IfaceRoomVo]()
    RoomManager.getInstance().getRooms
    for(room <- RoomManager.getInstance().getRooms.values()){
      //四人
      if(crazyType == 0 && !room.isInstanceOf[RoomPaijiu100] && room.isInstanceOf[RoomPaijiuCrazy] ){
        roomList.append(room.toVo(0))
      }
      //百人
      if(crazyType == 1 && room.isInstanceOf[RoomPaijiu100]){
        roomList.append(room.toVo(0))
      }
    }

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "getCrazyRoom", roomList.asJava), userId)
    0
  }
}