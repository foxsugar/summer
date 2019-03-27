package com.code.server.game.poker.paijiu

import java.lang

import com.code.server.constant.data.{DataManager, StaticDataProto}
import com.code.server.constant.response.ErrorCode
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.redis.service.RedisManager

/**
  * Created by sunxianping on 2019-03-22.
  */
class GamePaijiuCrazy extends GamePaijiu{





  override protected def getGroupScoreByName(name: String): Int = {

    DataManager.data.getPaijiuCardGroupScoreDataMap.get(name)


    val d: StaticDataProto.DataManager = DataManager.data
    val dataStr = DataManager.data.getRoomDataMap.get(this.roomPaijiu.getGameType).getPaijiuDataName
    val dataMethodName = "get" + dataStr + "GroupScoreDataMap"
    val method = d.getClass.getDeclaredMethod(dataMethodName)
    val m = method.invoke(d)
    val mp = m.asInstanceOf[java.util.Map[String,Object]]
    val o = mp.get(name)
    val scoreMethod = o.getClass.getDeclaredMethod("getScore")
    val score = scoreMethod.invoke(o)
    score.asInstanceOf[Int]
  }


  /**
    * 获得牌型分数
    *
    * @param group
    * @return
    */
  override def getGroupScore(group: String): Int = {

    val data = DataManager.data.getPaijiuCardGroupDataMap.get(group)
    //没有这个牌型或者不含这个牌型
    if(data == null || getNoGroupName().contains(data.getName)) {
      //两张牌的点数相加
      val cardArray = group.split(",")
      val card1 = cardArray(0)
      val card2 = cardArray(1)
      CARDSCORE(card1.toInt) + CARDSCORE(card2.toInt)
    }else{
      getGroupScoreByName(data.getName)
    }

  }


  def getNoGroupName(): Set[String] ={
    var set :Set[String] = Set()
    if(!Room.isHasMode(MODE_GUIZI,roomPaijiu.getOtherMode)) {
      set = set.+("ghost")
    }
    if(!Room.isHasMode(MODE_ZHADAN,roomPaijiu.getOtherMode)) {
      set = set.+("zhadan")
    }

    if(!Room.isHasMode(MODE_TIANJIU,roomPaijiu.getOtherMode)) {
      set = set.+("skynineking")
    }

    if(!Room.isHasMode(MODE_DIJIU,roomPaijiu.getOtherMode)) {
      set = set.+("fieldnine")
    }
    set
  }





  /**
    * 牌局结束
    */
  override protected def gameOver(): Unit = {
    compute()
    sendResult()
    genRecord()
    //切庄开始
    bankerBreakStart()
    //如果到了条件 自动切庄


    //大于10倍 小于20% 自动切庄

    sendFinalResult()
  }





  /**
    * 庄家切庄(牌局结束)
    *
    * @param userId
    * @return
    */
  override def bankerBreak(userId: lang.Long, flag: Boolean): Int = {
    if(state != STATE_BANKER_BREAK) return ErrorCode.NOT_BANKER
    if (userId != bankerId) return ErrorCode.NOT_BANKER
    if (flag) {
      //换庄家
      //把钱加到庄身上
      RedisManager.getUserRedisService.addUserMoney(bankerId,this.roomPaijiu.bankerScore)
      this.roomPaijiu.setBankerId(0)
      this.roomPaijiu.bankerScore = 0
      this.roomPaijiu.clearReadyStatus(true)
      sendFinalResult()

    } else {
      this.roomPaijiu.clearReadyStatus(true)
      this.roomPaijiu.startGame()
    }

    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreak", 0, userId)
    0
  }
}
