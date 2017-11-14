package com.code.server.game.poker.paijiu

import com.code.server.constant.data.DataManager
import com.code.server.game.room.kafka.MsgSender

import scala.collection.JavaConverters._

/**
  * Created by sunxianping on 2017/10/25.
  */
class GamePaijiu2Cards extends GamePaijiu {

  /**
    * 发牌
    */
  override protected def deal(): Unit = {

    val slidList = cards.sliding(2, 2).toList
    var count = 0
    for (playerInfo <- playerCardInfos.values) {
      playerInfo.cards ++= slidList(count)
      count += 1
      //发牌通知
      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
    }

  }


  /**
    * 是否所有人都开牌
    *
    * @return
    */
  override protected def isAllPlayerOpen(): Boolean = {
    playerCardInfos.count { case (_, playerInfo) => playerInfo.group1 == null } == 0
  }


  /**
    * 检测开牌是否合法
    *
    * @param playerCardInfo
    * @param group1
    * @param group2
    * @return
    */
  override protected def checkOpen(playerCardInfo: PlayerCardInfoPaijiu, group1: String, group2: String): Boolean = {
    val allCard = Array.concat(group1.split(",")).map(card => card.toInt).toList

    //开的牌和拥有的牌相同
    val isSame = playerCardInfo.cards.diff(allCard).isEmpty
    if (!isSame) return false
    true
  }


  override protected def getGroupScoreByName(name: String): Int = {
    DataManager.data.getLaotiePaijiuCardGroupScoreDataMap.get(name).getScore
  }

  /**
    * 获得牌型分数
    *
    * @param group
    * @return
    */
  override def getGroupScore(group: String): Int = {
    val name: String = DataManager.data.getPaijiuCardGroupDataMap.get(group).getName
    logger.info("cardgroupName : " + name)
    DataManager.data.getLaotiePaijiuCardGroupScoreDataMap.get(name).getScore
  }

  /**
    * 比较输赢并设置分数
    *
    * @param banker
    * @param other
    */
  override protected def compareAndSetScore(banker: PlayerCardInfoPaijiu, other: PlayerCardInfoPaijiu): Int = {
    val mix8Score = getGroupScoreByName(MIX_8)
    val bankerScore1 = getGroupScore(banker.group1)
    val otherScore1 = getGroupScore(other.group1)
    var result: Int = 0
    if (bankerScore1 >= otherScore1) result += 1
    if (bankerScore1 < otherScore1) result -= 1
    //庄家赢
    if (result > 0) {
      val changeScore = other.getBetScore(bankerScore1 >= mix8Score)
      banker.addScore(this.roomPaijiu, changeScore)
      other.addScore(this.roomPaijiu, -changeScore)
      roomPaijiu.addUserSocre(banker.userId, changeScore)
      roomPaijiu.addUserSocre(other.userId, -changeScore)
      other.winState = LOSE
    } else if (result < 0) {
      //闲家赢
      val changeScore = other.getBetScore(otherScore1 >= mix8Score)
      banker.addScore(this.roomPaijiu, -changeScore)
      other.addScore(this.roomPaijiu, changeScore)
      roomPaijiu.addUserSocre(banker.userId, -changeScore)
      roomPaijiu.addUserSocre(other.userId, changeScore)
      other.winState = WIN
    }
    result
  }
}
