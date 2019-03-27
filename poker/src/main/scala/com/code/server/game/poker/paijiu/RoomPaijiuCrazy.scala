package com.code.server.game.poker.paijiu

import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager
import com.code.server.util.{IdWorker, SpringUtil}

import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2019-03-25.
  */
class RoomPaijiuCrazy extends RoomPaijiu with PaijiuConstant{


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


  /**
    * 开房消费
    * @return
    */
  override def getNeedMoney: Int = {
    if(Room.isHasMode(MODE_WINNER_PAY, this.otherMode)) {
      return 0
    }
    super.getNeedMoney
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
    0
  }


  /**
    * 更新banker
    */
  def updateBanker(): Unit = {
    //现在没有banker
    if (this.bankerId == 0 && this.bankerList.nonEmpty) {
      //排队的第一个

      val userId = this.bankerList.head
      this.bankerId = userId
      this.bankerScore = this.bankerScoreMap(userId)
      this.bankerInitScore = this.bankerScoreMap(userId)
      //推送
      MsgSender.sendMsg2Player(new ResponseVo("gameService", "updatePaijiuBanker", Map("userId" -> userId).asJava), this.getUsers)
      //更新时间
      this.updateLastOperateTime()

      //改局数
      this.curGameNumber = 0

    }
  }



}


object RoomPaijiuCrazy extends Room {
  def createRoom(userId: Long, roomType: String, gameType: String, gameNumber: Int,clubId:String,clubRoomModel:String,isAA:Boolean,
                 robotType:Int, robotNum:Int, robotWinner:Int, isReOpen:Boolean, otherMode:Int, personNum:Int): Int = {
    val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
    val roomPaijiu = new RoomPaijiuCrazy
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

    MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createPaijiuCrazyRoom", roomPaijiu.toVo(userId)), userId)
    0
  }
}