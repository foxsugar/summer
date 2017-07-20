package com.code.server.gate.service

import com.code.server.constant.kafka.{IKafaTopic, KafkaMsgKey}
import com.code.server.constant.response.{ErrorCode, Notice, ResponseVo}
import com.code.server.kafka.MsgProducer
import com.code.server.redis.service.RedisManager
import com.code.server.util.SpringUtil

/**
  * Created by sunxianping on 2017/6/7.
  */
object ChatService {
  def sendMessageToOne(userId: Long, acceptUserId: Long, messageType: String, message: String): Int = {
    // messageType,1表示普通打字，2表示表情，3表示语音
    val sendUserId = userId
    val roomId = RedisManager.getUserRedisService.getRoomId(userId)
    if (roomId == null) return ErrorCode.CAN_NOT_NO_ROOM
    val sendNotice = new Notice
    val acceptNotice = new Notice
    sendNotice.setMessage("send message success!")
    sendNotice.setMessageType(messageType)
    acceptNotice.setMessage(message)
    acceptNotice.setMessageType(messageType)
    acceptNotice.setAcceptUserId("" + acceptUserId)
    acceptNotice.setSendUserId("" + sendUserId)


    sendMsg2kafka(roomId, acceptNotice)

    GateManager.sendMsg(new ResponseVo("chatService", "sendMessageToOne", sendNotice), userId)
    0
  }

  def sendMessage(userId: Long, messageType: String, message: String): Int = {
    // messageType,1表示普通打字，2表示表情，3表示语音
    val roomId = RedisManager.getUserRedisService.getRoomId(userId)
    if (roomId == null) return ErrorCode.CAN_NOT_NO_ROOM
    val sendNotice = new Notice
    val acceptNotice = new Notice
    sendNotice.setMessage("send message success!")
    sendNotice.setMessageType(messageType)
    sendNotice.setSendUserId("" + userId)
    acceptNotice.setMessage(message)
    acceptNotice.setMessageType(messageType)
    acceptNotice.setSendUserId("" + userId)

    sendMsg2kafka(roomId, acceptNotice)

    GateManager.sendMsg(new ResponseVo("chatService", "sendMessage", sendNotice), userId)
    0
  }

  def sendMsg2kafka(roomId: String, acceptNotice: Any): Unit = {
    val users = RedisManager.getRoomRedisService.getUsers(roomId)

    val gateId = GateManager.getGateId
    val sendMsg = (uid: Long) => {
      val serverid = RedisManager.getUserRedisService.getGateId(uid)
      val responsevo = new ResponseVo("chatService", "acceptMessage", acceptNotice)

      if (serverid != null) {
        if (serverid.toInt == gateId) {
          GateManager.sendMsg(responsevo, uid)
        } else {
          //发送kick通知 发送到kafka
          val kafkaSend = SpringUtil.getBean(classOf[MsgProducer])
          val msgkey = new KafkaMsgKey
          msgkey.setUserId(uid)
          kafkaSend.send2Partition(IKafaTopic.GATE_TOPIC, serverid.toInt, msgkey, responsevo)
        }
      }
    }

    users.stream().forEach(uid => sendMsg(uid))
  }
}
