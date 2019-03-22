package com.code.server.game.poker.paijiu

import java.lang.Long

import com.code.server.constant.kafka.KafkaMsgKey
import com.code.server.constant.response.ResponseVo
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
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
        val newRobotList = getRobotList(needNum)
        for(robot <- newRobotList){
          //发送加入房间
          sendJoinRoom(robot, roomPaijiu.getRoomId)
        }
      }

      if(roomPaijiu.getGame == null) return

      val gamePaijiu = room.getGame.asInstanceOf[GamePaijiu]

      val now = System.currentTimeMillis()

      //机器人下注
      doRobotBet(roomPaijiu, gamePaijiu, now)




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
          val two = Random.shuffle(betNum).head

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

        var group1 = ""
        var group2 = ""
        //是机器人的话
        if(room.robotList.contains(playerInfo.userId)){

          group1 = game.getCardsMaxScore(playerInfo.cards)._2
        }else{//默认

        }
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
    for(userId <- set.asScala){
      val uid:Long = Long.parseLong(userId)
      val roomId = RedisManager.getUserRedisService.getRoomId(uid)
      if(roomId == null){
        robotList = robotList.+:(uid)
        count+=1
        if(count>=num){
          robotList
        }
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


    MsgSender.sendMsg2Player("gamePaijiuService", "bet", 0, userId)
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

}
