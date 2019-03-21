package com.code.server.game.poker.paijiu

import java.lang.Long

import com.code.server.constant.kafka.KafkaMsgKey
import com.code.server.constant.response.ResponseVo
import com.code.server.game.room.Room
import com.code.server.game.room.service.IRobot
import com.code.server.kafka.MsgProducer
import com.code.server.redis.service.RedisManager
import com.code.server.util.SpringUtil
import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2019-03-20.
  */
class PaijiuRobot extends IRobot{
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

      var gamePaijiu = room.getGame.asInstanceOf[GamePaijiu]

      val now = System.currentTimeMillis()





    }
  }

  /**
    * 机器人下注
    * @param room
    * @param game
    */
  def doRobotBet(room:RoomPaijiu, game:GamePaijiu, time:Long): Unit ={
    if(!game.isInstanceOf[GamePaijiu100]) return
    val gamePaijiu = game.asInstanceOf[GamePaijiu100]

    //下注阶段5秒后机器人下注
    if(time - gamePaijiu.lastOperateTime > 1000 * 5){
      //没下注的机器人开始下注
      var count = 0
      for(userId <- room.robotList){

        val playerInfo = gamePaijiu.playerCardInfos(userId)

        //没下注
        if(playerInfo != null && playerInfo.bet == null){

          if(count<room.robotWinner){

          }
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
