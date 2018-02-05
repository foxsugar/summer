package com.code.server.game.poker.service

import com.code.server.constant.kafka.KafkaMsgKey
import com.code.server.constant.response.ResponseVo
import com.code.server.game.room.kafka.MsgSender
import com.code.server.util.JsonUtil
import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory

/**
  * Created by sunxianping on 2017/5/23.
  */
object MsgDispatch {
  private val logger = LoggerFactory.getLogger("MsgDispatch")

  def dispatch(record: ConsumerRecord[String, String]): Unit = {
    logger.info(record.toString)
    try {
      val key = record.key
      val value = record.value
      val msgKey = JsonUtil.readValue(key, classOf[KafkaMsgKey])
      val jsonNode = JsonUtil.readTree(value)
      val userId = msgKey.getUserId
      val roomId = msgKey.getRoomId
      val service = jsonNode.get("service").asText
      val method = jsonNode.get("method").asText
      val params = jsonNode.get("params")
      val code = dispatchAllMsg(userId, roomId, service, method, params)
      //客户端要的方法返回
      if (code != 0) {
        val vo = new ResponseVo(service, method, code)
        MsgSender.sendMsg2Player(vo, userId)
      }
    } catch {
      case e: Exception =>
        logger.error("poker 消息异常 ", e)
    }
  }
  private def dispatchAllMsg(userId: Long, roomId: String, service: String, method: String, params: JsonNode) = service match {
    case "gameService" =>
      GameService.dispatch(userId, method, roomId, params)
    case "gamePaijiuService" =>
      GameService.dispatch(userId, method, roomId, params)
    case "pokerRoomService" =>
      PokerRoomService.dispatch(userId, method, params)
    case "gameGuessService"=>
      GameService.dispatch(userId, method, roomId, params)
    case "reconnService" =>
      ReconnService.dispatch(userId, method, roomId)
    case "gameTTZService" =>
      GameService.dispatch(userId, method, roomId, params)
    case _ =>
      -1
  }
}