package com.code.server.game.poker.paijiu

import java.lang

import com.code.server.constant.response.{ErrorCode, GamePaijiuResult}
import com.code.server.game.room.kafka.MsgSender
import com.code.server.redis.service.RedisManager

import scala.collection.JavaConverters._
import scala.util.Random

/**
  * Created by sunxianping on 2019-06-17.
  */
class GameTuitongziGold extends GamePaijiu {

  override protected def initCards(): Unit = {
    //两局中的第一局 单数局
    val isFirst = roomPaijiu.getCurGameNumber % 2 == 1
    //单数局
    if (isFirst) {
      //初始牌并洗牌
      var cardList = List()
      for (i <- 0 to 36) {
        cardList.+:(i)
      }
      val rand = new Random
      roomPaijiu.cards = rand.shuffle(cardList)

      //拿出一半的牌
      val thisGameCards = roomPaijiu.cards.slice(0, 18)
      //本局的牌
      cards ++= thisGameCards
      //room中的牌删除本局的牌
      roomPaijiu.cards = roomPaijiu.cards.diff(thisGameCards)
      roomPaijiu.lastGameCards = thisGameCards
    } else {
      //双数局
      cards ++= roomPaijiu.cards.slice(0, 18)
    }



    //牌放到预先设定好的牌中

    var slidList = cards.sliding(2, 2).toList

    //分牌
    for (i <- 0 to 3) {
      commonCards += (i -> slidList(i))
    }
  }


  /**
    * 发牌
    */
  override protected def deal(): Unit = {

    val slidList = cards.sliding(4, 4).toList
    var count = 0
    for (playerInfo <- playerCardInfos.values) {
      playerInfo.cards ++= slidList(count)
      count += 1
      //发牌通知
      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
    }

  }


  override def bet(userId: lang.Long, one: Int, two: Int, three: Int, index: Int): Int = {
    if (this.state != STATE_BET) return ErrorCode.BET_PARAM_ERROR
    val playerInfo_option = playerCardInfos.get(userId)
    //玩家不存在
    if (playerInfo_option.isEmpty) return ErrorCode.NO_USER
    val playerCardInfoPaijiu = playerInfo_option.get
    //已经下过注
    //    if (playerCardInfoPaijiu.bet != null) return ErrorCode.ALREADY_BET
    //下注不合法

    val bet = new Bet(one, two, three, index)
    if (!checkBet(bet)) return ErrorCode.BET_PARAM_ERROR
    val myBetNum = one + two + three
    //金币牌九 下注不能大于身上的钱

    val myMoney = RedisManager.getUserRedisService.getUserMoney(userId)
    if (myMoney < playerCardInfoPaijiu.getBetNum() + one + two + three) {
      return ErrorCode.BET_PARAM_NO_MONEY
    }

    //总下注 不能大于锅底
    var betNum: Int = 0
    for (playerInfo <- this.playerCardInfos.values) {
      betNum += playerInfo.getBetNum()
    }
    if (betNum + myBetNum > this.roomPaijiu.bankerScore) {
      return ErrorCode.BET_PARAM_LIMIT
    }

    if (playerCardInfoPaijiu.bet == null) {
      playerCardInfoPaijiu.bet = bet

    } else {
      if (playerCardInfoPaijiu.bet.index != index) {
        return ErrorCode.BET_PARAM_ERROR
      }
      playerCardInfoPaijiu.bet.one += one
      playerCardInfoPaijiu.bet.two += two
      playerCardInfoPaijiu.bet.three += three
    }

    //    this.roomPaijiu.addUserSocre(userId, -myBetNum)

    val result = Map("userId" -> userId, "bet" -> bet)
    MsgSender.sendMsg2Player("gamePaijiuService", "betResult", result.asJava, roomPaijiu.users)
    MsgSender.sendMsg2Player("gamePaijiuService", "bet", 0, userId)

    //除去庄家全都下完注
    //    val count = playerCardInfos.count { case (uid, playerInfo) => uid != bankerId && playerInfo.bet != null }
    //    val isAllBet = count == users.size() - 1
    if (isBetFull()) {
      crapStart()
    }
    0
  }

  def isBetFull():Boolean={
    var betCount = 0
    this.playerCardInfos.values.foreach(player=>{
      betCount += player.getBetNum()
    })
    betCount>=this.roomPaijiu.bankerScore.toInt
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
//    if (!checkOpen(playerInfoOption.get, group1, group2)) return ErrorCode.OPEN_PARAM_ERROR
    playerInfoOption.get.group1 = group1
//    playerInfoOption.get.group2 = group2

    //记录最大牌型
//    val lastMax = roomPaijiu.getRoomStatisticsMap.get(userId).maxCardGroup
//    val lastMaxScore = if (lastMax == null) 0 else getGroupScore(lastMax)
//    val thisScore = getGroupScore(group1)
//    if (thisScore > lastMaxScore) {
//      roomPaijiu.getRoomStatisticsMap.get(userId).maxCardGroup = group1
//    }

    //是否已经全开牌
    val isAllOpen = isAllPlayerOpen()
    if (isAllOpen) {
      gameOver()
    }
    //开牌通知
    MsgSender.sendMsg2Player("gamePaijiuService", "openResult", Map("userId" -> userId).asJava, roomPaijiu.users)
    MsgSender.sendMsg2Player("gamePaijiuService", "open", 0, userId)
    0
  }


  /**
    * 获得牌型分数
    * @param cards
    * @return
    */
  def getCardScore(cards:List[Int]):Int={

    val card1 = cards.head
    val card2 = cards(1)
    val cardIndex1 = card1/4
    val cardIndex2 = card2/4
    val isSame = cardIndex1 == cardIndex2
    if(isSame) return (cardIndex1 + 1) * 10
    var sum = cardIndex1 + cardIndex2
    if(sum>=10) sum-=10
    return sum
  }

  /**
    * 结算
    */
  override def compute(): Unit = {
    val banker = playerCardInfos(bankerId)
    var winUsers: List[PlayerCardInfoPaijiu] = List()
    var resultSet: Set[Int] = Set()
    val bankerScore = getCardScore(commonCards(0))
    val otherScore1 = getCardScore(commonCards(1))
    val otherScore2 = getCardScore(commonCards(2))
    val otherScore3 = getCardScore(commonCards(3))
    playerCardInfos.foreach { case (uid, other) =>
      if (uid != bankerId && other.bet != null) {
        var result: Int = 0
        if(other.bet.one>0) {
          if (bankerScore>=otherScore1) result += other.bet.one else result -= other.bet.one
        }
        if(other.bet.two>0) {
          if (bankerScore>=otherScore2) result += other.bet.two else result -= other.bet.two
        }
        if(other.bet.three>0) {
          if (bankerScore>=otherScore3) result += other.bet.three else result -= other.bet.three
        }

        banker.addScore(roomPaijiu,result)
        other.addScore(roomPaijiu,-result)

        roomPaijiu.bankerScore += result
        //庄的分最后再加
        //          roomPaijiu.addUserSocre(banker.userId, changeScore)
        roomPaijiu.addUserSocre(other.userId, -result)
        roomPaijiu.logRoomStatistics(other.userId, -result)
        roomPaijiu.logRoomStatistics(banker.userId, result)


        //庄家赢
        if (result > 0) {
          other.winState = LOSE
        } else if (result < 0) {
          other.winState = WIN
          winUsers = winUsers.+:(other)
        }else{
          logger.info("和了")
        }

      }
    }



    //记录胜负平
    val gamePaijiuResult = new GamePaijiuResult()

    doLogRecord(gamePaijiuResult, 1, otherScore1 - bankerScore)
    doLogRecord(gamePaijiuResult, 2, otherScore2 - bankerScore)
    doLogRecord(gamePaijiuResult, 3, otherScore3 - bankerScore)

    this.roomPaijiu.winnerIndex.append(gamePaijiuResult)

    if(this.roomPaijiu.winnerIndex.size>10){
      this.roomPaijiu.winnerIndex.remove(0)
    }


    //全赢或全输
    if (resultSet.size == 1) {
      val bankerStatiseics = this.roomPaijiu.getRoomStatisticsMap.get(bankerId)
      if (resultSet.contains(WIN)) bankerStatiseics.winAllTime += 1
      if (resultSet.contains(LOSE)) bankerStatiseics.loseAllTime += 1
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
    //    updateLastOperateTime()
    this.lastOperateTime = System.currentTimeMillis
    if(isAutoBreakBanker()) {
      state = STATE_BANKER_BREAK
      bankerBreak(this.bankerId, flag = true)
    }else{
      //      if(this.roomPaijiu.curGameNumber==1) {
      //        this.roomPaijiu.clearReadyStatus(true)
      ////        this.roomPaijiu.startGame()
      //      }else{
      bankerBreakStart()
      //      }
    }
  }

  def isAutoBreakBanker():Boolean ={
    //大于10倍 小于20% 自动切庄
    //todo 是否有多少把 自动下庄
    this.roomPaijiu.bankerScore > 10 * this.roomPaijiu.bankerInitScore || this.roomPaijiu.bankerScore < this.roomPaijiu.bankerInitScore * 20 /100 || this.roomPaijiu.curGameNumber>24

  }
  /**
    * 庄家切庄(牌局结束)
    *
    * @param userId
    * @return
    */
  override def bankerBreak(userId: lang.Long, flag: Boolean): Int = {
    if (userId != bankerId) return ErrorCode.NOT_BANKER
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreakResp", Map("userId"->userId, "flag"->flag).asJava, this.roomPaijiu.getUsers)

    if (flag) {
      //换庄家
      //把钱加到庄身上
      //抽水
      //      val winScore:Double = this.roomPaijiu.bankerScore - this.roomPaijiu.bankerInitScore
      //      var rebate:Double = 0
      //      if(winScore > 0) {
      //        //抽水
      //        val s = winScore * this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_BET).asInstanceOf[String].toDouble / 100
      //        val finalScore = this.roomPaijiu.bankerScore - s
      //
      //
      //        //返利
      //        rebate =  winScore * this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_REBATE100).asInstanceOf[String].toDouble / 100
      //        this.roomPaijiu.sendCenterAddRebate(userId, rebate)
      //
      //        RedisManager.getUserRedisService.addUserMoney(bankerId,finalScore)
      //      }else{
      RedisManager.getUserRedisService.addUserGold(bankerId,this.roomPaijiu.bankerScore)
      //      }
      //      this.roomPaijiu.lastBankerInitScore = this.roomPaijiu.bankerInitScore
      this.roomPaijiu.setBankerId(0)
      this.roomPaijiu.bankerScore = 0
      this.roomPaijiu.clearReadyStatus(true)

      //      val room100 = roomPaijiu.asInstanceOf[RoomPaijiu100]
      //      room100.updateBanker()
      this.roomPaijiu.pushScoreChange()

      //百人不结束
    } else {
      this.roomPaijiu.clearReadyStatus(true)
      //      this.roomPaijiu.startGame()
    }

    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreak", 0, userId)
    0
  }

}
