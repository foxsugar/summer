package com.code.server.game.poker.paijiu


import java.lang.Long

import com.code.server.constant.kafka.KafkaMsgKey
import com.code.server.constant.response.ResponseVo
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.Room
import com.code.server.game.room.service.IRobot
import com.code.server.kafka.MsgProducer
import com.code.server.redis.service.RedisManager
import com.code.server.util.SpringUtil

import scala.collection.JavaConverters._
import scala.util.Random

/**
  * Created by sunxianping on 2019-03-20.
  */
class PaijiuRobot extends IRobot with PaijiuConstant{
  override def execute(): Unit = {}


  override def doExecute(room: Room): Unit = {
    if(room == null || !room.isInstanceOf[RoomPaijiu]){
      return
    }
    var roomPaijiu = room.asInstanceOf[RoomPaijiu]

    //机器人加入的逻辑
    //机器人可加入 并且 game为空时 机器人加入
    if(roomPaijiu.robotType !=0 && roomPaijiu.getGame == null) {
      val robotNum = roomPaijiu.robotList.size
      //小于所需机器人数量
      if(robotNum<roomPaijiu.robotNum) {
        val needRobotNum = roomPaijiu.robotNum - robotNum
        val needPeopleNum = roomPaijiu.getPersonNumber  - roomPaijiu.users.size()
        val needNum = if (needRobotNum<needPeopleNum) needRobotNum else needPeopleNum
        if(needNum != 0){

          val newRobotList = getRobotList(needNum)
          for(robot <- newRobotList){
            //发送加入房间
            sendJoinRoom(robot, roomPaijiu.getRoomId)
          }
        }
      }

      val now = System.currentTimeMillis()

      //开始游戏
      doStartGame(roomPaijiu,now)

      if(roomPaijiu.getGame == null) return


      val gamePaijiu = room.getGame.asInstanceOf[GamePaijiu]

      //机器人下注
      doRobotBet(roomPaijiu, gamePaijiu, now)

      //开牌
      doAutoOpen(roomPaijiu, gamePaijiu, now)

      //自动切锅 或者继续




    }
  }


  /**
    * 开始游戏
    * @param room
    * @param now
    */
  def doStartGame(room:RoomPaijiu, now:Long): Unit ={
    if(room.getGame == null) {
      if(room.isInstanceOf[RoomPaijiu100]){
        val rp = room.asInstanceOf[RoomPaijiu100]
        //更新banker
        rp.updateBanker()
        //选定庄家后10秒开局
        if((now - rp.getLastOperateTime) > 10000 && rp.getBankerId != 0){
          sendStartGame(rp)
        }
      }

    }
  }

  def doBreakBanker(room:RoomPaijiu, game:GamePaijiu, now :Long): Unit ={
    if(game.state == STATE_BANKER_BREAK){
      //10秒自动 继续
      if(now - game.lastOperateTime > 1000 * 10){
        sendBreakBanker(game.bankerId, room.getRoomId)
      }
    }
  }
  /**
    * 机器人下注
    * @param room
    * @param game
    */
  def doRobotBet(room:RoomPaijiu, game:GamePaijiu, time:Long): Unit ={
    if(!game.isInstanceOf[GamePaijiu100]) return
    if(game.state != STATE_BET) return
    val gamePaijiu = game.asInstanceOf[GamePaijiu100]

    //下注阶段5秒后机器人下注
    if(!gamePaijiu.isRobotBet && time - gamePaijiu.lastOperateTime > 1000 * 5){
      //没下注的机器人开始下注
      var count = 0
      val winIndex = gamePaijiu.getMaxScoreCardIndex()
      gamePaijiu.isRobotBet = true
      val loseIndex = List(1,2,3).filter(_!=winIndex)
      //下注选择
      val betNum = List(5, 10, 50)

      for(userId <- room.robotList){
        val playerInfo = gamePaijiu.playerCardInfos(userId)
        //没下注
        if(playerInfo != null && playerInfo.bet == null){

          val one = Random.shuffle(betNum).head
          //只下一道
          val two = 0

          //下赢得注
          if(count<room.robotWinner){
            sendBet(playerInfo.userId, room.getRoomId, one, two, 0, winIndex)
            count += 1
          }else{//下输的注
            val index = Random.shuffle(loseIndex).head
            sendBet(playerInfo.userId, room.getRoomId, one, two, 0, index)
          }
        }
      }
    }
  }


  /**
    * 自动开牌
    * @param room
    * @param game
    * @param time
    */
  def doAutoOpen(room:RoomPaijiu, game:GamePaijiu, time:Long): Unit ={
//    if(!game.isInstanceOf[GamePaijiu100]) return
    if(game.state != STATE_OPEN) return

    //10秒自动开牌
    if(time - game.lastOperateTime <= 1000 * 10) return

    for(playerInfo <- game.playerCardInfos.values){

      if(playerInfo.cards.nonEmpty && playerInfo.group1==null) {

        val finalGroup = game.getMaxOpenGroup(playerInfo.cards)

        sendOpenMsg(playerInfo.userId, room.getRoomId, finalGroup._1, finalGroup._2)

      }
    }


  }


  /**
    * 获得所需的机器人
    * @param num
    * @return
    */
  private def getRobotList(num:Int):List[Long] = {
    val set:java.util.Set[String] =  RedisManager.getUserRedisService.getRobotPoolUser
    var robotList:List[Long] = List()
    var count = 0
    import scala.collection.JavaConversions._
    for(userId <- set){
      val uid:Long = Long.parseLong(userId)
      val roomId = RedisManager.getUserRedisService.getRoomId(uid)
      if(roomId == null){
        count+=1
        if(count>=num){
          return robotList
        }
        robotList = robotList.+:(uid)
      }
    }


    robotList
  }


  /**
    * 下注
    * @param userId
    * @param roomId
    * @param one
    * @param two
    * @param three
    * @param index
    */
  private def sendBet(userId:Long,roomId:String,one:Int, two:Int, three:Int,index:Int): Unit ={
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("one"->one, "two"->two, "three"->three,"index"->index)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "bet", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }
  /**
    * 发送加入房间
    * @param userId
    * @param roomId
    */
  private def sendJoinRoom(userId:Long, roomId:String): Unit ={

      val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
      val msgKey: KafkaMsgKey = new KafkaMsgKey
      msgKey.setRoomId(roomId)
      msgKey.setPartition(partition)
      msgKey.setUserId(userId)
      val put = Map("userId"->userId)
      val result: ResponseVo = new ResponseVo("roomService", "joinRoom", put.asJava)
      SpringUtil.getBean(classOf[MsgProducer]).send2Partition("roomService", partition, msgKey, result)
    }


  /**
    * 发送开牌消息
    * @param userId
    * @param roomId
    * @param group1
    * @param group2
    */
  private def sendOpenMsg(userId:Long, roomId:String, group1:String, group2:String ): Unit ={
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("group1"->group1, "group2"->group2)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "open", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }


  /**
    * 发送开始游戏消息
    * @param room
    */
  def sendStartGame(room: Room): Unit = {
    val roomId: String = room.getRoomId
    val partition: Int = SpringUtil.getBean(classOf[ServerConfig]).getServerId
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(0)
    val result: ResponseVo = new ResponseVo("roomService", "startAuto", Map().asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("roomService", partition, msgKey, result)
  }


  /**
    * 发送切庄
    * @param userId
    * @param roomId
    */
  def sendBreakBanker(userId:Long, roomId:String): Unit ={
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("flag"->false)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "bankerBreak", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }
}