package com.code.server.gate.service

import com.code.server.constant.kafka.{IKafaTopic, KickUser}
import com.code.server.constant.response.{ErrorCode, ResponseVo}
import com.code.server.gate.config.ServerConfig
import com.code.server.gate.kafka.MsgProducer
import com.code.server.gate.util.SpringUtil
import com.code.server.redis.service.{RoomRedisService, UserRedisService}
import com.code.server.util.JsonUtil
import io.netty.channel.ChannelHandlerContext
import net.sf.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired

/**
  * Created by sunxianping on 2017/5/26.
  */
object MsgDispatch {


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
          val key =  SpringUtil.getBean(classOf[ServerConfig]).getServerId.toString
          SpringUtil.getBean(classOf[MsgProducer]).send(service,key, msg)

        case "roomService" => roomService_dispatch(userId,method,params,msg)

        case _ =>
          val partition = getPartitionByUserId(userId)
          if(partition == null) {
            GateManager.sendMsg(userId,new ResponseVo("roomService",method,ErrorCode.CAN_NOT_NO_ROOM))
            return
          }
          //玩家id做key
          val key = userId.toString
          SpringUtil.getBean(classOf[MsgProducer]).send(service,partition.toInt,key, msg)

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
    method match {
        //加入房间有roomId
      case "joinRoom" =>
        val roomId = params.getString("roomId")
        val partition = getPartitionByRoomId(roomId)
        if(partition == null) {
          GateManager.sendMsg(userId,new ResponseVo("roomService",method,ErrorCode.CAN_NOT_NO_ROOM))
        }else{
          SpringUtil.getBean(classOf[MsgProducer]).send(IKafaTopic.ROOM_TOPIC,partition,msg)
        }
      case _ =>
        val partition = getPartitionByUserId(userId)
        if(partition == null) {
          GateManager.sendMsg(userId,new ResponseVo("roomService",method,ErrorCode.CAN_NOT_NO_ROOM))
        }else{
          SpringUtil.getBean(classOf[MsgProducer]).send(IKafaTopic.ROOM_TOPIC,partition,msg)
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
  def getPartitionByUserId(userId : Long):String = {
    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val roomId = userRedisService.getRoomId(userId)
    if(roomId == null) {
      return null
    }
    getPartitionByRoomId(roomId)


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
    val loginGateId = userRedisService.getGateId(userId)
    //登录过
    if (loginGateId != null) {

      val serverConfig = SpringUtil.getBean(classOf[ServerConfig])
      //两个netty连接在同一个gate上
      if (serverConfig.getServerId == loginGateId) {

        //ctx 不是同一个
        if (ctx != GateManager.getUserNettyCtxByUserId(userId)) {
          UserSevice.sendExit(userId)
        }

      } else {
        //发送kick通知 发送到kafka
        val kafkaSend = SpringUtil.getBean(classOf[MsgProducer])
        val kickUser = new KickUser
        kickUser.setId(userId)
        kafkaSend.send(IKafaTopic.INNER_GATE_TOPIC, loginGateId, kickUser)
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
