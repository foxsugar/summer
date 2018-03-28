package com.code.server.game.poker.service

import com.code.server.constant.game.CardStruct
import com.code.server.constant.response.ErrorCode
import com.code.server.game.poker.cow.GameCow
import com.code.server.game.poker.doudizhu.{GameDouDiZhu, GameDouDiZhuGold}
import com.code.server.game.poker.hitgoldflower.GameHitGoldFlower
import com.code.server.game.poker.guess.GameGuessCar
import com.code.server.game.poker.paijiu.{GameGoldPaijiu, GamePaijiu}
import com.code.server.game.poker.pullmice.GamePullMice
import com.code.server.game.poker.tuitongzi.GameTuiTongZi
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
      case x:GameHitGoldFlower =>dispatchGameHGFService(userId,method,game.asInstanceOf[GameHitGoldFlower],params)
      case x:GameGuessCar =>dispatchGameGuessService(userId,method,game.asInstanceOf[GameGuessCar],params)
      case x:GameCow =>dispatchGameCowService(userId,method,game.asInstanceOf[GameCow],params)
      case x:GameTuiTongZi =>dispatchGameTTZService(userId,method,game.asInstanceOf[GameTuiTongZi],params)
      case x:GamePullMice =>dispatchGamePullMiceService(userId,method,game.asInstanceOf[GamePullMice],params)
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

  private def dispatchGamePullMiceService(userId:Long, method: String, game: GamePullMice, params: JsonNode) = method match {
    case "bet1" =>
      val zhu = params.path("zhu").asInt(0)
      game.bet(userId, zhu, 1);
    case "bet2" =>
      val zhu = params.path("zhu").asInt(0)
      game.bet(userId, zhu, 2);
    case "bet3" =>
      val zhu = params.path("zhu").asInt(0)
      game.bet(userId, zhu, 3);
    case "bet4" =>
      val zhu = params.path("zhu").asInt(0)
      game.bet(userId, zhu, 4);
    case "fiveStepClose" =>
      val zhu = params.path("zhu").asInt(0)
      game.fiveStepClose(userId, zhu);
    case "cheat" =>
      val cheatId = params.path("cheatId").asInt(0)
      val uid = params.path("uid").asInt(0)
      game.cheatMsg(cheatId, uid)
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGameTTZService(userId:Long,method: String, game: GameTuiTongZi, params: JsonNode) = method match {
    case "continueBanker" =>
      val zhuang = params.path("isZhuang").asBoolean();
      game.continueBanker(zhuang, userId);
    case "bet" =>
      val zhu = params.path("zhu").asInt(0)
      game.bet(userId, zhu);
    case "crap"=>
      game.crap(userId)
    case "open"=>
      val firstId = params.path("firstId").asLong()
//      game.open(userId, firstId);
      game.open(userId, firstId);
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
      val cardsPattern = params.path("cardsPattern").asInt(0)
      game.exchange(userId, cardsPattern);
    case "setTestUser" =>
      game.setTestUser(userId)
    case "cheat" =>
//      game.cheat(userId)
      val cheatId = params.path("cheatId").asLong()
      val uid = params.path("uid").asLong()
      game.cheat(cheatId, uid)
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGamePJService(userId:Long,method: String, game: GamePaijiu, params: JsonNode) = method match {
    case "bet" =>
      val one = params.path("one").asInt(0)
      val two = params.path("two").asInt(0)
      val three = params.path("three").asInt(0)
      game.bet(userId,one,two,three)
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
    case "catchBanker" =>
      game.asInstanceOf[GameGoldPaijiu].catchBanker(userId);
    case "setTestUser" =>
      game.setTestUser(userId)
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGameHGFService(userId:Long,method: String, game: GameHitGoldFlower, params: JsonNode):Int = method match {
    case "raise" =>
      val addChip = params.path("addChip").asLong(0)
      game.raise(userId,addChip);
    case "call"=>
      game.call(userId);
    case "fold" =>
      game.fold(userId);
    case "see" =>
      game.see(userId);
    case "kill" =>
      val accepterId = params.path("accepterId").asLong(0)
      game.kill(userId,accepterId);
    case "perspective" =>
      game.perspective(userId);
    case "changeCard" =>
      val userId = params.path("userId").asLong(0)
      val cardType = params.path("type").asText()
      game.changeCard(userId,cardType);
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGameCowService(userId:Long,method: String, game: GameCow, params: JsonNode):Int = method match {
    case "raise" =>
      val addChip = params.path("addChip").asLong(0)
      game.raise(userId,addChip);
    case "compare"=>
      game.compare(userId);
    case "perspective" =>
      game.perspective(userId);
    case "changeCard" =>
      val userId = params.path("userId").asLong(0)
      val cardType = params.path("type").asInt()
      game.changeCard(userId,cardType);
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  private def dispatchGameGuessService(userId:Long,method: String, game: GameGuessCar, params: JsonNode):Int = method match {
    case "raise" =>
      val userId = params.path("userId").asLong(0)
      val addChip = params.path("addChip").asDouble(0)
      val color = params.path("color").asInt(0)
      game.raise(userId,addChip,color);
    case "getRemainTime"=>
      game.getRemainTime(userId)
    case "look"=>
      game.look(userId)
    case "change"=>
      val color = params.path("color").asInt()
      game.change(userId,color)
    case _ =>
      ErrorCode.REQUEST_PARAM_ERROR
  }

  def getGame(roomId : String):IfaceGame = {
    RoomManager.getRoom(roomId).getGame
  }



}
