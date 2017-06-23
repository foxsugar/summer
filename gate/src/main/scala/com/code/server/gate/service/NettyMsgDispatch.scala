package com.code.server.gate.service

import java.util.Comparator
import java.util.stream.Collectors

import com.code.server.constant.kafka.{IKafaTopic, KafkaMsgKey, KickUser}
import com.code.server.constant.response.{ErrorCode, ReconnectResp, ResponseVo}
import com.code.server.gate.config.ServerConfig
import com.code.server.kafka.MsgProducer
import com.code.server.redis.config.ServerInfo
import com.code.server.redis.service.{RedisManager, RoomRedisService, UserRedisService}
import com.code.server.util.{JsonUtil, SpringUtil}
import com.fasterxml.jackson.databind.JsonNode
import io.netty.channel.ChannelHandlerContext

import scala.util.Random

/**
  * Created by sunxianping on 2017/5/26.
  */
object NettyMsgDispatch {

  val MAHJONG:String = "MAHJONG"
  val POKER:String = "POKER"

  /**
    * 分发消息
    *
    * @param msg
    * @param ctx
    */
  def dispatch(msg: Object, ctx: ChannelHandlerContext): Unit = {
    val jsonObject = msg.asInstanceOf[JsonNode]
    val service = jsonObject.get("service").asText()
    val method = jsonObject.get("method").asText()
    val params = jsonObject.get("params")

    val userId = ctx.channel().attr(GateManager.attributeKey).get()

    val result =
      service match {
        case "gateService" => gateService_dispatch(method, params, jsonObject, ctx)
        //直接发到kafka
        case "userService" =>
          //gateId 做key
          val partition = SpringUtil.getBean(classOf[ServerConfig]).getServerId
          val kafkaKey = new KafkaMsgKey
          kafkaKey.setUserId(userId)
          kafkaKey.setPartition(partition)
          val keyJson = JsonUtil.toJson(kafkaKey)
          SpringUtil.getBean(classOf[MsgProducer]).send(service, keyJson, msg)

        case "roomService" => roomService_dispatch(userId, service, method, params, msg)

        case "reconnService" => reconnService_dispatch(userId, msg)

        case "mahjongRoomService" =>
          val msgKey = new KafkaMsgKey
          msgKey.setUserId(userId)

          val server = getSortedServer(MAHJONG)
          if(server == null) {
            GateManager.sendMsg(new ResponseVo(service, method, ErrorCode.GAMESERVER_NOT_OPEN), userId)
            return
          }
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(service, server.getServerId, JsonUtil.toJson(msgKey), msg)

        case "pokerRoomService" =>
          val msgKey = new KafkaMsgKey
          msgKey.setUserId(userId)
          val server = getSortedServer(POKER)
          if(server == null) {
            GateManager.sendMsg(new ResponseVo(service, method, ErrorCode.GAMESERVER_NOT_OPEN), userId)
            return
          }
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(service, server.getServerId, JsonUtil.toJson(msgKey), msg)

        case "chatService" =>
          chatService_dispatch(userId, method, params)

        //游戏逻辑
        case _ =>
          val roomAndPartition = getPartitionByUserId(userId)
          if (roomAndPartition == null) {
            GateManager.sendMsg(new ResponseVo(service, method, ErrorCode.CAN_NOT_NO_ROOM), userId)
            return
          }
          //玩家id做key
          val roomId = roomAndPartition._1
          val partition = roomAndPartition._2
          val msgKey = new KafkaMsgKey
          msgKey.setUserId(userId)
          msgKey.setRoomId(roomId)
          msgKey.setPartition(partition.toInt)
          //按service的名字 分topic
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(service, partition.toInt, msgKey, msg)

      }

  }


  private def sort(servers: java.util.List[ServerInfo]): Unit = {
    val com = new Comparator[ServerInfo] {
      override def compare(o1: ServerInfo, o2: ServerInfo): Int = {
        if (o1.getSort > o2.getSort) {
          -1
        } else {
          1
        }
      }
    }
    servers.sort(com)
  }

  /**
    * 通过服务器类型获得一个服务器实例
    * @param serverType
    * @return
    */
  def getSortedServer(serverType: String): ServerInfo = {

    val servers = RedisManager.getGameRedisService.getAllServer.stream().filter(
      serverInfo => serverInfo.getStatus == 0 && serverInfo.getServerType == serverType
    ).collect(Collectors.toList())
    //排序
    sort(servers)
    val size = servers.size()
    if (size == 0) {
      return null
    }
    val rand = new Random()
    val randId = rand.nextInt(size)
    servers.get(randId)
  }

  /**
    * 网关消息分发
    *
    * @param method
    * @param params
    * @param jsonObject
    * @param ctx
    */
  def gateService_dispatch(method: String, params: JsonNode, jsonObject: JsonNode, ctx: ChannelHandlerContext): Unit = {
    method match {
      case "login" =>
        val userId = params.get("userId").asLong()
        val token = params.get("token").asText()
        loginGate(userId, token, ctx)
      case "heart" =>
        val userId = params.get("userId").asLong()
        val map = Map("time" -> System.currentTimeMillis())
        GateManager.sendMsg(new ResponseVo("gateService", "heart", map), userId)


    }
  }

  /**
    * 房间消息分发
    *
    * @param userId
    * @param method
    * @param params
    * @param msg
    */
  def roomService_dispatch(userId: Long, service: String, method: String, params: JsonNode, msg: Object): Unit = {
    val msgKey = new KafkaMsgKey
    msgKey.setUserId(userId)
    method match {
      //加入房间有roomId
      case "joinRoom" =>
        val roomId = params.get("roomId").asText()
        val partition = getPartitionByRoomId(roomId)
        if (partition == null) {
          GateManager.sendMsg(new ResponseVo("roomService", method, ErrorCode.CAN_NOT_NO_ROOM), userId)
        } else {
          msgKey.setRoomId(roomId)
          msgKey.setPartition(partition.toInt)
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(service, partition.toInt, msgKey, msg)
        }


      //解散房间等其他消息
      case _ =>
        val roomAndPartition = getPartitionByUserId(userId)
        if (roomAndPartition == null) {
          GateManager.sendMsg(new ResponseVo("roomService", method, ErrorCode.CAN_NOT_NO_ROOM), userId)
        } else {
          val roomId = roomAndPartition._1
          val partition = roomAndPartition._2
          msgKey.setRoomId(roomId)
          msgKey.setPartition(partition.toInt)
          SpringUtil.getBean(classOf[MsgProducer]).send2Partition(service, partition.toInt, msgKey, msg)
        }
    }
  }

  def reconnService_dispatch(userId: Long, msg: Object): Unit = {
    val roomId = RedisManager.getUserRedisService.getRoomId(userId)
    if (roomId == null) {
      val reconnectResp = new ReconnectResp
      reconnectResp.setExist(false)
      val vo = new ResponseVo("reconnService", "reconnection", reconnectResp)
      GateManager.sendMsg(vo, userId)

    } else {
      val partition = getPartitionByRoomId(roomId)
      val msgKey = new KafkaMsgKey
      msgKey.setRoomId(roomId)
      msgKey.setUserId(userId)
      msgKey.setPartition(partition.toInt)
      SpringUtil.getBean(classOf[MsgProducer]).send2Partition(IKafaTopic.RECONN_TOPIC, partition.toInt, msgKey, msg)
    }
  }

  private def chatService_dispatch(userId: Long, method: String, params: JsonNode) = {

    method match {
      case "sendMessageToOne" =>
        val acceptUserId = params.get("acceptUserId").asLong()
        val messageType = params.get("messageType").asText()
        val message = params.get("message").asText()
        ChatService.sendMessageToOne(userId, acceptUserId, messageType, message)

      case "sendMessage" =>
        val messageType = params.get("messageType").asText()
        val message = params.get("message").asText()
        ChatService.sendMessage(userId, messageType, message)

      case _ =>
        ErrorCode.REQUEST_PARAM_ERROR

    }
  }

  /**
    * 通过roomId找到分区
    *
    * @param roomId
    * @return
    */
  def getPartitionByRoomId(roomId: String): String = {
    val redisService = SpringUtil.getBean(classOf[RoomRedisService])
    redisService.getServerId(roomId)
  }

  /**
    * 通过userId找到分区
    *
    * @param userId
    * @return
    */
  def getPartitionByUserId(userId: Long): (String, String) = {
    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val roomId = userRedisService.getRoomId(userId)
    if (roomId == null) {
      return null
    }
    val partition = getPartitionByRoomId(roomId)
    if (partition == null) {
      return null
    }
    (roomId, partition)

  }


  /**
    * 登录gate
    *
    * @param userId
    * @param token
    * @param ctx
    * @return
    */
  def loginGate(userId: Long, token: String, ctx: ChannelHandlerContext): Int = {
    val userRedisService = SpringUtil.getBean(classOf[UserRedisService])
    val token_redis = userRedisService.getToken(userId)
    if (token != token_redis) {
      ctx.writeAndFlush(JsonUtil.toJson(new ResponseVo("gateService", "login", ErrorCode.ID_TOKEN_NOT_MATCH)))
      return ErrorCode.ID_TOKEN_NOT_MATCH
    }
    val loginGateId = userRedisService.getGateId(userId)
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
    GateManager.sendMsg(new ResponseVo("gateService", "login", userBean.toVo), userId)

    return 0

  }


  def main(args: Array[String]): Unit = {
    val kickUser = new KickUser

    println(JsonUtil.toJson(kickUser))

    print("9999")
  }
}
