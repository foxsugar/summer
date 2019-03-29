package com.code.server.game.poker.paijiu

import java.lang

import com.code.server.constant.response.ErrorCode
import com.code.server.game.room.kafka.MsgSender
import com.code.server.redis.service.RedisManager

import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2019-03-18.
  */
class GamePaijiu100 extends GamePaijiuCrazy {


  override protected def initCards(): Unit = {
    super.initCards()
    //牌放到预先设定好的牌中

    val slidList = cards.sliding(4, 4).toList


    //分牌
    for (i <- 0 to 3) {
      commonCards += (i -> slidList(i))
    }

  }



  /**
    * 牌局结束
    */
  override protected def gameOver(): Unit = {
    compute()
    sendResult()
    //不记记录日志
//    genRecord()
    //切庄开始
    if(isAutoBreakBanker()) {
      bankerBreak(this.bankerId, flag = true)
    }else{
      bankerBreakStart()
    }
  }


  override def isAutoBreakBanker():Boolean ={
    //大于10倍 小于20% 自动切庄
    this.roomPaijiu.bankerScore > 10 * this.roomPaijiu.bankerInitScore || this.roomPaijiu.bankerScore < this.roomPaijiu.bankerInitScore * 20 /100

  }


  /**
    * 发牌
    */
  override protected def deal(): Unit = {
    //    //测试的发牌
    //    if (this.roomPaijiu.isTest && this.roomPaijiu.getCurGameNumber % 2 == 0 && this.roomPaijiu.testUserId != 0) {
    //      val (maxCards, newCards) = PaijiuCardUtil.getMaxGroupAndNewCards(cards)
    //      val testPlayer = playerCardInfos(this.roomPaijiu.testUserId)
    //      testPlayer.cards = maxCards
    //
    //      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", testPlayer.cards.asJava, this.roomPaijiu.testUserId)
    //      val slidList = newCards.sliding(4, 4).toList
    //      var count = 0
    //
    //
    //      for (playerInfo <- playerCardInfos.values if playerInfo.userId != this.roomPaijiu.testUserId) {
    //        playerInfo.cards ++= slidList(count)
    //        count += 1
    //        //发牌通知
    //        MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
    //      }
    //
    //      //状态置回
    //      this.roomPaijiu.testUserId = 0
    //    } else {

    for (playerInfo <- playerCardInfos.values) {
      //庄家
      if (bankerId == playerInfo.userId) {
        playerInfo.cards ++= commonCards(0)
      } else {
        if (playerInfo.bet != null) {
          val index = playerInfo.bet.index
          playerInfo.cards ++= commonCards(index)
        }
        MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
      }
    }
  }


  /**
    * 获得最大分数的索引
    *
    * @return
    */
  def getMaxScoreCardIndex(): Int = {

    var max = 0
    var index = 1
    for (i <- 1 to 3) {
      val score = getCardsMaxScore(commonCards(i))._1
      if (score > max) {
        max = score
        index = i
      }
    }
    index
  }



  /**
    * 庄家切庄(牌局结束)
    *
    * @param userId
    * @return
    */
  override def bankerBreak(userId: lang.Long, flag: Boolean): Int = {
    if (userId != bankerId) return ErrorCode.NOT_BANKER
    if (flag) {
      //换庄家
      //把钱加到庄身上
      RedisManager.getUserRedisService.addUserMoney(bankerId,this.roomPaijiu.bankerScore)
      this.roomPaijiu.setBankerId(0)
      this.roomPaijiu.bankerScore = 0
      this.roomPaijiu.clearReadyStatus(true)

      val room100 = roomPaijiu.asInstanceOf[RoomPaijiu100]
      room100.updateBanker()

      //百人不结束
    } else {
      this.roomPaijiu.clearReadyStatus(true)
      this.roomPaijiu.startGame()
    }

    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreak", 0, userId)
    0
  }



}
