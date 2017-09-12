package com.code.server.game.poker.service

import com.code.server.constant.game.CardStruct
import com.code.server.constant.response.ErrorCode
import com.code.server.game.poker.doudizhu.{GameDouDiZhu, GameDouDiZhuGold}
import com.code.server.game.poker.paijiu.GamePaijiu
import com.code.server.game.room.IfaceGame
import com.code.server.game.room.service.RoomManager
import com.code.server.util.JsonUtil
import com.fasterxml.jackson.databind.JsonNode

/**
  * Created by sunxianping on 2017/6/7.
  */
object GameService {

  def dispatch(userId:Long, method:String, roomId:String, params:JsonNode):Int = {
    val game = getGame(roomId)
    game match {
      case x:GameDouDiZhu =>dispatchGameDDZService(userId,method,game.asInstanceOf[GameDouDiZhu],params)
      case x:GameDouDiZhuGold =>dispatchGameDDZGoldService(userId,method,game.asInstanceOf[GameDouDiZhuGold],params)
      case x:GamePaijiu =>dispatchGamePJService(userId,method,game.asInstanceOf[GamePaijiu],params)
    }

  }

  private def dispatchGameDDZService(userId:Long,method: String, game: GameDouDiZhu, params: JsonNode) = method match {
    case "jiaoDizhu" =>
      val isJiao = params.get("isJiao").asBoolean()
      val score = params.path("score").asInt(0)
//      params.
      game.jiaoDizhu(userId, isJiao, score)
    case "qiangDizhu" =>

      val isQiang = params.get("isQiang").asBoolean()
      game.qiangDizhu(userId, isQiang)
    case "play" =>
      val json = params.path("cards").toString
      val cardStruct = JsonUtil.readValue(json, classOf[CardStruct])
      game.play(userId, cardStruct)
    case "pass" =>
      game.pass(userId)
    case "sayHello" =>
      game.sayHello()
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGameDDZGoldService(userId:Long,method: String, game: GameDouDiZhuGold, params: JsonNode) = method match {
    case "jiaoDizhu" =>
      val isJiao = params.get("isJiao").asBoolean()
      val score = params.path("score").asInt(0)
      //      params.
      game.jiaoDizhu(userId, isJiao, score)
    case "qiangDizhu" =>

      val isQiang = params.get("isQiang").asBoolean()
      game.qiangDizhu(userId, isQiang)
    case "play" =>
      val json = params.path("cards").toString
      val cardStruct = JsonUtil.readValue(json, classOf[CardStruct])
      game.play(userId, cardStruct)
    case "pass" =>
      game.pass(userId)
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGamePJService(userId:Long,method: String, game: GamePaijiu, params: JsonNode) = method match {
    case "bet" =>
      val one = params.path("one").asInt(0)
      val two = params.path("two").asInt(0)
      game.bet(userId,one,two)
    case "crap"=>
      game.crap(userId)
    case "open"=>
      val group1 = params.path("group1").asText()
      val group2 = params.path("group2").asText()
      game.open(userId, group1, group2)
    case "fightForBanker" =>
      val flag = params.path("flag").asBoolean()
      game.fightForBanker(userId, flag)
    case "bankerSetScore" =>
      val score = params.path("score").asInt()
      game.bankerSetScore(userId, score)

    case "bankerBreak" =>
      val flag = params.path("flag").asBoolean()
      game.bankerBreak(userId, flag)


    case "exchange" =>
      game.exchange(userId)
    case "setTestUser" =>
      game.setTestUser(userId)
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  def getGame(roomId : String):IfaceGame = {
    RoomManager.getRoom(roomId).getGame
  }



}
