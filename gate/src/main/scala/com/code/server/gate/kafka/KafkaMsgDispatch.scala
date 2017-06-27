package com.code.server.gate.kafka

import com.code.server.constant.kafka.{IKafkaMsg, IkafkaMsgId, KafkaMsgKey, KickUser}
import com.code.server.gate.service.{GateManager, UserSevice}
import com.code.server.util.JsonUtil
import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * Created by sunxianping on 2017/5/31.
  */
object KafkaMsgDispatch {

  def send2Client(userId:Long, msg:Object): Unit ={
    val ctx = GateManager.getUserNettyCtxByUserId(userId)
    if(ctx != null ) {
      ctx.writeAndFlush(msg)
    }
  }

  def handleGate2GateMsg(record: ConsumerRecord[_, _]):Unit = {
    val json = record.value().toString
    val msg = JsonUtil.readValue(json, classOf[IKafkaMsg])

  }

  def dispatchInnerGate(record: ConsumerRecord[String,String]):Unit = {
    val keyStr =record.key()
    val valueStr = record.value()
    val msgKey = JsonUtil.readValue(keyStr,classOf[KafkaMsgKey])
    val msgId = msgKey.msgId
    msgId match {
      case IkafkaMsgId.KAFKA_MSG_ID_GATE_KICK_USER =>{
        val kickUser = JsonUtil.readValue(valueStr,classOf[KickUser])
        val userId = kickUser.getId
        UserSevice.sendExit(userId)
      }

    }
  }

  def main(args: Array[String]): Unit = {
    val kickUser = new KickUser
    kickUser.setId(1)

    val json = JsonUtil.toJson(kickUser)
    println(json)

    val msg = JsonUtil.readValue(json, classOf[IKafkaMsg])
    println(msg)

  }
}
