package com.code.server.game.poker.paijiu

import java.{lang, util}

import com.code.server.constant.game.IGameConstant
import com.code.server.constant.response.{ErrorCode, GamePaijiuResult, GameResultPaijiu}
import com.code.server.game.room.Room
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

    var slidList = cards.sliding(4, 4).toList


    //两张牌模式
    if(Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)){
      slidList = cards.sliding(2, 2).toList
    }

    //分牌
    for (i <- 0 to 3) {
      commonCards += (i -> slidList(i))
    }

  }


  /**
    * 开始游戏
    *
    * @param users
    * @param room
    */
  override def startGame(users: util.List[lang.Long], room: Room): Unit = {
    roomPaijiu = room.asInstanceOf[RoomPaijiu]
    loadData()
    //    this.room = roomPaijiu
    //实例化玩家
    initPlayer()
    //码牌
    initCards()

    bankerId = roomPaijiu.getBankerId
    //下注阶段开始
    betStart()

  }


  override protected def sendResult(): Unit = {
    var gameResult = new GameResultPaijiu
    val bankerPlayer = this.playerCardInfos(this.roomPaijiu.getBankerId)
    //抽水
    var choushui:Double = 0
    if(bankerPlayer.getScore() > 0) {
      choushui = bankerPlayer.getScore() * this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_BET).asInstanceOf[String].toDouble / 100
      //给庄家上级返利
      bankerPlayer.score = bankerPlayer.score - choushui

      var rebate =  bankerPlayer.getScore() * this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_REBATE100).asInstanceOf[String].toDouble / 100
      this.roomPaijiu.sendCenterAddRebate(this.roomPaijiu.getBankerId, rebate)

      //四张牌不返利
//      if(Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)){
//        this.roomPaijiu.sendCenterAddThreeRebate(this.roomPaijiu.getBankerId, bankerPlayer.getScore(),1)
//      }
    }
    this.playerCardInfos.values.foreach(playerInfo => gameResult.getPlayerCardInfos.add(playerInfo.toVo))
    this.roomPaijiu.bankerScore -= choushui
    gameResult.setBankerScore(this.roomPaijiu.bankerScore)

    this.commonCards.foreach(t=>{
      val index = t._1
      val cards = t._2
      if(index !=0){
        val otherGroup = getMaxOpenGroup(cards)
        var s = otherGroup._1
        if(otherGroup._2 != null) {
          s = otherGroup._1 +"&" + otherGroup._2
        }
        gameResult.getCardMap.put(index, s)
      }
    })

    gameResult.setSfp(this.roomPaijiu.winnerIndex.last)

    this.playerCardInfos.foreach(player=>{
      if(player._2.winState!=0) {

        val num = 2D * player._2.getBetNum() /100
        this.roomPaijiu.sendCenterAddContribute(player._1, num)
      }
    })

    MsgSender.sendMsg2Player("gamePaijiuService", "gameResult", gameResult, roomPaijiu.users)
    this.roomPaijiu.pushScoreChange()
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




  /**
    * 下注
    *
    * @param userId
    * @param one
    * @param two
    * @return
    */
  override def bet(userId: lang.Long, one: Int, two: Int, three: Int, index: Int): Int = {
    if(this.state != STATE_BET) return ErrorCode.BET_PARAM_ERROR
    val playerInfo_option = playerCardInfos.get(userId)
    //玩家不存在
    if (playerInfo_option.isEmpty) return ErrorCode.NO_USER
    val playerCardInfoPaijiu = playerInfo_option.get
    //已经下过注
//    if (playerCardInfoPaijiu.bet != null) return ErrorCode.ALREADY_BET
    //下注不合法

    val bet = new Bet(one, two,three,index)
    if (!checkBet(bet)) return ErrorCode.BET_PARAM_ERROR
    val myBetNum = one + two + three
    //金币牌九 下注不能大于身上的钱

    val myMoney = RedisManager.getUserRedisService.getUserMoney(userId)
    if(myMoney<playerCardInfoPaijiu.getBetNum() + one + two + three) {
      return ErrorCode.BET_PARAM_NO_MONEY
    }

    //总下注 不能大于锅底
    var betNum:Int = 0
    for(playerInfo <- this.playerCardInfos.values){
      betNum += playerInfo.getBetNum()
    }
    if(betNum + myBetNum > this.roomPaijiu.bankerScore) {
      return ErrorCode.BET_PARAM_LIMIT
    }

    if(playerCardInfoPaijiu.bet == null) {
      playerCardInfoPaijiu.bet = bet

    }else{
      if(playerCardInfoPaijiu.bet.index != index) {
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
    betCount>=this.roomPaijiu.bankerScore * 80 /100
  }


  override def isAutoBreakBanker():Boolean ={
    //大于10倍 小于20% 自动切庄
    this.roomPaijiu.bankerScore > 10 * this.roomPaijiu.bankerInitScore || this.roomPaijiu.bankerScore < this.roomPaijiu.bankerInitScore * 20 /100 || this.roomPaijiu.curGameNumber>24

  }


  /**
    * 开牌
    *
    * @param userId
    * @param group1
    * @param group2
    * @return
    */
  override def open(userId: lang.Long, group1: String, group2: String): Int = {
    val playerInfoOption = playerCardInfos.get(userId)
    if (playerInfoOption.isEmpty) return ErrorCode.NO_USER
    if(playerInfoOption.get.group1 != null) {
      return ErrorCode.OPEN_PARAM_ERROR
    }
    //开牌是否合法
    if (!checkOpen(playerInfoOption.get, group1, group2)) return ErrorCode.OPEN_PARAM_ERROR
    playerInfoOption.get.group1 = group1
    playerInfoOption.get.group2 = group2

    //记录最大牌型
    val lastMax = roomPaijiu.getRoomStatisticsMap.get(userId).maxCardGroup
    val lastMaxScore = if (lastMax == null) 0 else getGroupScore(lastMax)
    val thisScore = getGroupScore(group1)
    if (thisScore > lastMaxScore) {
      roomPaijiu.getRoomStatisticsMap.get(userId).maxCardGroup = group1
    }

    //是否已经全开牌
    val isAllOpen = isAllPlayerOpen()
    if (isAllOpen) {
      gameOver()
    }else{
      if(!Room.isHasMode(MODE_2CARD,roomPaijiu.otherMode) && userId == roomPaijiu.getBankerId){
        otherOpenStart()
      }
    }
    //开牌通知
    MsgSender.sendMsg2Player("gamePaijiuService", "openResult", Map("userId" -> userId).asJava, roomPaijiu.users)
    MsgSender.sendMsg2Player("gamePaijiuService", "open", 0, userId)


    0
  }



  /**
    * 是否所有人都开牌
    *
    * @return
    */
  override protected def isAllPlayerOpen(): Boolean = {
    for(playerInfo <- this.playerCardInfos.values) {
      if(playerInfo.userId == this.bankerId){
        if(playerInfo.group1 == null){
          return false
        }
      }else{
        if(playerInfo.bet!= null && playerInfo.group1 == null){
          return false
        }
      }
    }
    true
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
      }
      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
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
        RedisManager.getUserRedisService.addUserMoney(bankerId,this.roomPaijiu.bankerScore)
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

  /**
    * 记录胜负平日志
    */
  override def dataLog(): Unit = {
    val gamepaijiuResult = new GamePaijiuResult()
    val bankerCards = this.commonCards(0)

    val banker = playerCardInfos(this.bankerId)

//    val bankerGroup = getMaxOpenGroup(bankerCards)
    val bankerScore1 = getGroupScore(banker.group1)
    val bankerScore2 = getGroupScore(banker.group2)
    this.commonCards.foreach(t=>{
      val index = t._1
      val cards = t._2
      if(index !=0){
        val otherGroup = getMaxOpenGroup(cards)
        val otherScore1 = getGroupScore(otherGroup._1)
        val otherScore2 = getGroupScore(otherGroup._2)

        var result: Int = 0
        if (bankerScore1 >= otherScore1) result += 1
        if (bankerScore1 < otherScore1) result -= 1
        if(!Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)){

          if (bankerScore2 >= otherScore2) result += 1
          if (bankerScore2 < otherScore2) result -= 1
        }

        if(result>0) result = 1
        if(result<0) result = -1
        doLogRecord(gamepaijiuResult,index,-result)
      }
    })

    this.roomPaijiu.winnerIndex.append(gamepaijiuResult)
    if(this.roomPaijiu.winnerIndex.size>10){
      this.roomPaijiu.winnerIndex.remove(0)
    }
  }
}
