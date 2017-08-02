package com.code.server.game.poker.paijiu


import java.lang.Long
import java.util

import com.code.server.constant.data.DataManager
import com.code.server.constant.response._
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.game.room.{Game, Room}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.util.Random


/**
  * Created by sunxianping on 2017/7/21.
  */
class GamePaijiu extends Game with PaijiuConstant {


  val logger: Logger = LoggerFactory.getLogger(classOf[GamePaijiu])

  var cards: List[Int] = List()

  var playerCardInfos: Map[Long, PlayerCardInfoPaijiu] = Map()

  var roomPaijiu: RoomPaijiu = _

  var bankerId: Long = -1L

  //状态
  var state: Int = STATE_START

  var room:Room = _


  /**
    * 开始游戏
    *
    * @param users
    * @param room
    */
  override def startGame(users: util.List[Long], room: Room): Unit = {
    roomPaijiu = room.asInstanceOf[RoomPaijiu]
    this.room = roomPaijiu
    //实例化玩家
    init()

    //两局中的第一局 单数局
    val isFirst = roomPaijiu.cards.size == ALL_CARD_NUM
    //单数局
    if (isFirst) {

      //初始牌并洗牌

      val cardList = DataManager.data.getPaijiuCardDataMap.values().asScala.map(card => card.getCard).toList
      val rand = new Random
      roomPaijiu.cards ++= rand.shuffle(cardList)

      //拿出一半的牌
      val thisGameCards = roomPaijiu.cards.slice(0, 15)
      //本局的牌
      cards ++= thisGameCards
      //room中的牌删除本局的牌
      roomPaijiu.cards = roomPaijiu.cards.diff(thisGameCards)
      roomPaijiu.lastGameCards ++= thisGameCards


    } else {
      cards ++= roomPaijiu.cards

    }
    //轮庄
    val isChangeBanker = roomPaijiu.getCurGameNumber == roomPaijiu.getCurGameNumber/roomPaijiu.getUsers.size()
    if(isChangeBanker) {
      logger.info("轮庄 === curgameNum :" + roomPaijiu.getCurGameNumber)
      val nextBanker = nextTurnId(roomPaijiu.getBankerId)
      roomPaijiu.setBankerId(nextBanker)
    }
    //设置庄家
    bankerId = Predef.long2Long(roomPaijiu.getBankerId)

    //下注阶段开始
    betStart()
  }


  /**
    * 初始化
    */
  protected def init(): Unit = {
    this.users.addAll(roomPaijiu.getUsers)
    roomPaijiu.getUsers.forEach(uid => {
      val playerInfo = new PlayerCardInfoPaijiu
      playerInfo.userId = uid
      this.playerCardInfos += (uid -> playerInfo)
    })

  }

  /**
    * 发牌
    */
  protected def fapai(): Unit = {
    val slidList = cards.sliding(4,4).toList
    var count = 0
    for(playerInfo <- playerCardInfos.values){
      playerInfo.cards ++= slidList(count)
      count += 1
    }
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
    if (playerCardInfoPaijiu.bet != null) return ErrorCode.ALREADY_BET
    //下注不合法

    val bet = new Bet(one, two)
    if (!checkBet(bet)) return ErrorCode.BET_PARAM_ERROR

    playerCardInfoPaijiu.bet = bet


    val result = Map("userId"->userId,"bet"->bet)
    MsgSender.sendMsg2Player("gamePaijiuService","betResult",result.asJava,users)
    MsgSender.sendMsg2Player("gamePaijiuService","bet",0,userId)

    //除去庄家全都下完注
    val isAllBet = playerCardInfos.count { case (uid, playerInfo) => uid != bankerId && playerInfo.bet != null } == 0
    if (isAllBet) {
      openStart()
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
    MsgSender.sendMsg2Player("gamePaijiuService","open",0,userId)
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
    if(!isSame) return false

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
    sendResult()
    this.room.clearReadyStatus(true)

    sendFinalResult()

  }


  /**
    * 结算
    */
  protected def compute(): Unit = {
    val banker = playerCardInfos(bankerId)
    playerCardInfos.foreach { case (uid, playerInfo) =>
      if (uid != bankerId) {
        compareAndSetScore(banker, playerInfo)
      }
    }
  }

  /**
    * 牌局结果
    */
  protected def sendResult():Unit = {
    val gameResult = new GameResultPaijiu
    this.playerCardInfos.values.foreach(playerInfo=>gameResult.getPlayerCardInfos.add(playerInfo.toVo))
    MsgSender.sendMsg2Player("gamePaijiuService", "gameResult",gameResult,this.users)
  }

  /**
    * 最终结算版
    */
  protected def sendFinalResult():Unit = {
    if(this.room.getCurGameNumber > this.room.getGameNumber) {
      val userOfResultList = this.room.getUserOfResult
      // 存储返回
      val gameOfResult = new GameOfResult
      gameOfResult.setUserList(userOfResultList)
      MsgSender.sendMsg2Player("gameService", "gamePaijiuFinalResult", gameOfResult, users)
      RoomManager.removeRoom(room.getRoomId)
    }
  }
  /**
    * 比较输赢并设置分数
    *
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
    if (result > 0) {
      val changeScore = other.getBetScore(bankerScore1 >= mix8Score)
      banker.score += changeScore
      other.score -= changeScore
      roomPaijiu.addUserSocre(banker.userId, changeScore)
      roomPaijiu.addUserSocre(other.userId, -changeScore)
      other.winState = lose
    } else if (result < 0) {
      //闲家赢
      val changeScore = other.getBetScore(otherScore1 >= mix8Score)
      banker.score -= changeScore
      other.score += changeScore
      roomPaijiu.addUserSocre(banker.userId, -changeScore)
      roomPaijiu.addUserSocre(other.userId, changeScore)
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
    DataManager.data.getPaijiuCardGroupScoreDataMap.get(name).getScore
  }

  /**
    * 转换为下注状态
    */
  protected def betStart(): Unit = {
    state = STATE_BET

    val param = Map("bankerId"->this.bankerId)
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "betStart", param.asJava, users)
  }

  /**
    * 转换为开牌状态
    */
  protected def openStart(): Unit = {
    //发牌扔色子
    crap()


    state = STATE_OPEN
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "openStart", 0, users)
  }

  /**
    * 掷骰子
    */
  private def crap(): Unit = {
    val rand = new Random()
    val num = rand.nextInt(11) + 2
    val result = Map("num" -> num)
    MsgSender.sendMsg2Player("gamePaijiuService", "randSZ", result.asJava, users)

  }




  override def toVo(watchUser: scala.Long): IfaceGameVo = {
    val gamePaijiuVo = new GamePaijiuVo
    gamePaijiuVo.bankerId = this.bankerId
    gamePaijiuVo.state = this.state
    this.playerCardInfos.foreach{case (userId,playerInfo)=>
      gamePaijiuVo.playerCardInfos.put(userId,playerInfo.toVo(watchUser))
    }
    gamePaijiuVo

  }

}

