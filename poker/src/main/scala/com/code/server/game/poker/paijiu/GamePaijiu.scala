package com.code.server.game.poker.paijiu


import java.lang.Long
import java.util

import com.code.server.constant.data.DataManager
import com.code.server.constant.response.ErrorCode
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.{Game, Room}

import scala.collection.JavaConverters._
import scala.util.Random


/**
  * Created by sunxianping on 2017/7/21.
  */
class GamePaijiu extends Game with PaijiuConstant {


  var cards: List[Int] = List()

  val playerCardInfos: Map[Long, PlayerCardInfoPaijiu] = Map()

  var roomPaijiu: RoomPaijiu = _

  var bankerId: Long = -1L

  //状态
  var state: Int = STATE_START


  /**
    * 开始游戏
    *
    * @param users
    * @param room
    */
  override def startGame(users: util.List[Long], room: Room): Unit = {
    roomPaijiu = room.asInstanceOf[RoomPaijiu]
    init()

    //先下注

    //两局中的第一局 单数局
    val isFirst = roomPaijiu.cards.size == ALL_CARD_NUM
    //摇色子决定发牌
    if (isFirst) {
      //发牌扔色子
      randFapai()
      cards ++= roomPaijiu.cards
    } else {
      //拿出一半的牌
      val thisGameCards = roomPaijiu.cards.slice(0, 15)
      //本局的牌
      cards ++= thisGameCards
      //room中的牌删除本局的牌
      roomPaijiu.cards = roomPaijiu.cards.diff(thisGameCards)
      roomPaijiu.lastGameCards ++= thisGameCards


      //轮庄
      val nextBanker = nextTurnId(roomPaijiu.getBankerId)
      roomPaijiu.setBankerId(nextBanker)
    }
    bankerId = roomPaijiu.getBankerId
  }


  /**
    * 初始化
    */
  protected def init(): Unit = {
    this.users.addAll(roomPaijiu.getUsers)
    roomPaijiu.getUsers.forEach(uid =>{
      val playerInfo = new PlayerCardInfoPaijiu
      playerInfo.userId = uid
      this.playerCardInfos + (uid -> playerInfo)
    })

  }

  /**
    * 发牌
    */
  protected def fapai(): Unit = {
    this.playerCardInfos.values.foreach(playerCardInfos => {
      for (i <- 0 to 4) {
        playerCardInfos.cards.+:(cards.asJava.remove(0))
      }
    })

  }

  /**
    * 下注
    *
    * @param userId
    * @param one
    * @param two
    * @return
    */
  def bet(userId: Long, one: Int, two: Int): Int = {
    val playerInfo_option = playerCardInfos.get(userId)
    //玩家不存在
    if (playerInfo_option.isEmpty) return ErrorCode.NO_USER
    val playerCardInfoPaijiu = playerInfo_option.get
    //已经下过注
    if (playerCardInfoPaijiu.bet == null) return ErrorCode.ALREADY_BET
    //下注不合法

    val bet = new Bet(one, two)
    if (!checkBet(bet)) return ErrorCode.BET_PARAM_ERROR

    playerCardInfoPaijiu.bet = bet

    //除去庄家全都下完注
    val isAllBet = playerCardInfos.count { case (uid, playerInfo) => uid == bankerId || playerInfo.bet == null } == 1
    if (isAllBet) {
      open_start
    }
    0
  }

  /**
    * 验证下注
    *
    * @param bet
    * @return
    */
  protected def checkBet(bet: Bet): Boolean = {
    bet.one > 0 && bet.two > 0
  }

  /**
    * 开牌
    *
    * @param userId
    * @param group1
    * @param group2
    * @return
    */
  def open(userId: Long, group1: String, group2: String): Int = {
    val playerInfoOption = playerCardInfos.get(userId)
    if (playerInfoOption.isEmpty) return ErrorCode.NO_USER
    //开牌是否合法
    if (!checkOpen(playerInfoOption.get, group1, group2)) return ErrorCode.OPEN_PARAM_ERROR
    playerInfoOption.get.group1 = group1
    playerInfoOption.get.group2 = group2

    //是否已经全开牌
    val isAllOpen = playerCardInfos.count { case (uid, playerInfo) => playerInfo.group1 != null && playerInfo.group2 != null } == 0
    if (isAllOpen) {
      gameOver()
    }
    0
  }

  /**
    * 检测开牌是否合法
    *
    * @param playerCardInfo
    * @param group1
    * @param group2
    * @return
    */
  protected def checkOpen(playerCardInfo: PlayerCardInfoPaijiu, group1: String, group2: String): Boolean = {
    val allCard = Array.concat(group1.split(","), group2.split(",")).map(card => card.toInt).toList

    //开的牌和拥有的牌相同
    val isSame = playerCardInfo.cards.diff(allCard).isEmpty

    //第一组不小于第二组牌
    val score1 = getGroupScore(group1)
    val score2 = getGroupScore(group2)
    score1 >= score2
  }

  /**
    * 牌局结束
    */
  protected def gameOver(): Unit = {
    compute()

  }

  /**
    * 结算
    */
  protected def compute(): Unit = {
    val banker = playerCardInfos(bankerId)
    playerCardInfos.foreach { case (uid, playerInfo) =>
      if(uid != bankerId){
        compareAndSetScore(banker,playerInfo)
      }
    }
  }

  /**
    * 比较输赢并设置分数
    * @param banker
    * @param other
    */
  protected def compareAndSetScore(banker: PlayerCardInfoPaijiu, other: PlayerCardInfoPaijiu): Unit = {
    val mix8Score = getGroupScore(MIX_8)
    val bankerScore1 = getGroupScore(banker.group1)
    val bankerScore2 = getGroupScore(banker.group2)
    val otherScore1 = getGroupScore(other.group1)
    val otherScore2 = getGroupScore(other.group2)
    var result = 0
    if (bankerScore1 > otherScore1) result += 1
    if (bankerScore1 < otherScore1) result -= 1
    if (bankerScore2 > otherScore2) result += 1
    if (bankerScore2 < otherScore2) result -= 1
    //庄家赢
    if(result>0) {
      val changeScore = other.getBetScore(bankerScore1>=mix8Score)
      banker.score += changeScore
      other.score -= changeScore
      roomPaijiu.addUserSocre(banker.userId,changeScore)
      roomPaijiu.addUserSocre(other.userId,-changeScore)
      other.winState = lose
    }else if(result<0){//闲家赢
      val changeScore = other.getBetScore(otherScore1>=mix8Score)
      banker.score -= changeScore
      other.score += changeScore
      roomPaijiu.addUserSocre(banker.userId,-changeScore)
      roomPaijiu.addUserSocre(other.userId,changeScore)
      other.winState = win
    }
  }

  /**
    * 获得牌型分数
    *
    * @param group
    * @return
    */
  protected def getGroupScore(group: String): Int = {
    val name = DataManager.data.getPaijiuCardGroupDataMap.get(group)
    DataManager.data.getPaijiuCardGroupScoreDataMap.get(name).getScore.toInt
  }

  /**
    * 转换为下注状态
    */
  protected def bet_start: Unit = {
    state = STATE_BET

  }

  /**
    * 转换为开牌状态
    */
  protected def open_start: Unit = {
    state = STATE_OPEN

  }

  /**
    * 随机发牌
    */
  private def randFapai(): Unit = {
    val rand = new Random()
    val num = rand.nextInt(11) + 2
    val result = Map("num" -> num)
    MsgSender.sendMsg2Player("gameService", "randSZ", result.asJava, users)

  }


}

