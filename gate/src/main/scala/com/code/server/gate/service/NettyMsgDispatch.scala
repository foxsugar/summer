package com.code.server.gate.service

import com.code.server.constant.kafka.{IKafaTopic, KafkaMsgKey, KickUser}
import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.gate.config.ServerConfig
import com.code.server.gate.kafka.MsgProducer
import com.code.server.redis.service.{RoomRedisService, UserRedisService}
import com.code.server.util.{JsonUtil, SpringUtil}
import io.netty.channel.ChannelHandlerContext
import net.sf.json.JSONObject

/**
  * Created by sunxianping on 2017/5/26.
  */
object NettyMsgDispatch {


  /**
    * 分发消息
    * @param msg
    * @param ctx
    */
  def dispatch(msg: Object, ctx: ChannelHandlerContext): Unit = {
    val jsonObject = msg.asInstanceOf[JSONObject]
    val service = jsonObject.getString("service")
    val method = jsonObject.getString("method")
    val params = jsonObject.getJSONObject("params")

    val userId = ctx.channel().attr(GateManager.attributeKey).get()

    val result =
      service match {
        case "gateService" => gateService_dispatch(method, params, jsonObject, ctx)
        //直接发到kafka
        case "userService" =>
          //gateId 做key
          val partition =  SpringUtil.getBean(classOf[ServerConfig]).getServerId
          val kafkaKey = new KafkaMsgKey
          kafkaKey.setUserId(userId)
          kafkaKey.setPartition(partition)
          val keyJson = JsonUtil.toJson(kafkaKey)
          SpringUtil.getBean(classOf[MsgProducer]).send(service,keyJson, msg)

        case "roomService" => roomService_dispatch(userId,method,params,msg)

        case _ =>
          val roomAndPartition = getPartitionByUserId(userId)
          if(roomAndPartition == null) {
            GateManager.sendMsg(userId,new ResponseVo("roomService",method,ErrorCode.CAN_NOT_NO_ROOM))
            return
          }
          //玩家id做key
          val roomId = roomAndPartition._1
          val partition = roomAndPartition._2
          val msgKey = new KafkaMsgKey
          msgKey.setUserId(userId)
          msgKey.setRoomId(roomId)
          msgKey.setPartition(partition.toInt);
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(service,partition.toInt,JsonUtil.toJson(msgKey), msg)

      }

  }


  /**
    * 网关消息分发
    * @param method
    * @param params
    * @param jsonObject
    * @param ctx
    */
  def gateService_dispatch(method: String, params: JSONObject, jsonObject: JSONObject, ctx: ChannelHandlerContext): Unit = {
    method match {
      case "loginGate" =>
        val userId = params.getLong("userId")
        val token = params.getString("token")

        loginGate(userId, token, ctx)


    }
  }

  /**
    * 房间消息分发
    * @param userId
    * @param method
    * @param params
    * @param msg
    */
  def roomService_dispatch(userId: Long, method: String, params: JSONObject, msg: Object): Unit = {
    val msgKey = new KafkaMsgKey
    msgKey.setUserId(userId)
    method match {
        //加入房间有roomId
      case "joinRoom" =>
        val roomId = params.getString("roomId")
        val partition = getPartitionByRoomId(roomId)
        if(partition == null) {
          GateManager.sendMsg(userId,new ResponseVo("roomService",method,ErrorCode.CAN_NOT_NO_ROOM))
        }else{
          msgKey.setRoomId(roomId)
          msgKey.setPartition(partition.toInt)
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(IKafaTopic.ROOM_TOPIC,partition.toInt,JsonUtil.toJson(msgKey),msg)
        }


      case _ =>
        val roomAndPartition = getPartitionByUserId(userId)
        if(roomAndPartition == null) {
          GateManager.sendMsg(userId,new ResponseVo("roomService",method,ErrorCode.CAN_NOT_NO_ROOM))
        }else{
          val roomId = roomAndPartition._1
          val partition = roomAndPartition._2
          msgKey.setRoomId(roomId)
          msgKey.setPartition(partition.toInt)
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(IKafaTopic.ROOM_TOPIC,partition.toInt,JsonUtil.toJson(msgKey),msg)
        }
    }
  }

  /**
    * 通过roomId找到分区
    * @param roomId
    * @return
    */
  def getPartitionByRoomId(roomId : String):String = {
    val redisService = SpringUtil.getBean(classOf[RoomRedisService])
    redisService.getServerId(roomId)
  }

  /**
    * 通过userId找到分区
    * @param userId
    * @return
    */
  def getPartitionByUserId(userId : Long):(String,String) = {
    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val roomId = userRedisService.getRoomId(userId)
    if(roomId == null) {
      return null
    }
    val partition = getPartitionByRoomId(roomId)
    if(partition == null) {
      return null
    }
    (roomId,partition)

  }


  /**
    * 登录gate
    * @param userId
    * @param token
    * @param ctx
    * @return
    */
  def loginGate(userId: Long, token: String, ctx: ChannelHandlerContext): Int = {
    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val token_redis = userRedisService.getToken(userId)
    if (token != token_redis) {
      return ErrorCode.ID_TOKEN_NOT_MATCH
    }
    val loginGateId  = userRedisService.getGateId(userId)
    //登录过
    if (loginGateId != null) {
      val loginGateIdInt = loginGateId.toInt

      val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
      //两个netty连接在同一个gate上
      if (serverConfig.getServerId == loginGateIdInt) {

        //ctx 不是同一个
        if (ctx != GateManager.getUserNettyCtxByUserId(userId)) {
          UserSevice.sendExit(userId)
        }

      } else {
        //发送kick通知 发送到kafka
        val kafkaSend = SpringUtil.getBean(classOf[MsgProducer])
        val kickUser = new KickUser
        kickUser.setId(userId)
        kafkaSend.send2Partition(IKafaTopic.INNER_GATE_TOPIC, loginGateIdInt, kickUser)
      }
    }

    //登录操作
    val userBean = UserSevice.doLogin(userId, ctx)
    GateManager.sendMsg(userId, userBean)

    return 0

  }


  def main(args: Array[String]): Unit = {
    val kickUser = new KickUser

    println(JsonUtil.toJson(kickUser))

    print("9999")
  }
}
