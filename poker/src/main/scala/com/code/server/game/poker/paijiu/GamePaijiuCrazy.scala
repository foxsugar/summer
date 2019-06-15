package com.code.server.game.poker.paijiu

import java.{lang, util}

import com.code.server.constant.data.StaticDataProto.CrazyPaijiuCardGroupData
import com.code.server.constant.data.{DataManager, StaticDataProto}
import com.code.server.constant.game.IGameConstant
import com.code.server.constant.response.{ErrorCode, GameOfResult, GamePaijiuResult}
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.redis.service.RedisManager

import scala.collection.JavaConverters._
import scala.util.Random
/**
  * Created by sunxianping on 2019-03-22.
  */
class GamePaijiuCrazy extends GamePaijiu{


  /**
    * 开始游戏
    *
    * @param users
    * @param room
    */
  override def startGame(users: util.List[lang.Long], room: Room): Unit = {
      roomPaijiu = room.asInstanceOf[RoomPaijiu]
      loadData()
    //实例化玩家
      initPlayer()
      //码牌
      initCards()

      bankerId = roomPaijiu.getBankerId
      updateLastOperateTime()

      room.getCurGameNumber match {
        case 1 => {
          fightForBankerStart()
        }
        case _ => betStart()
      }
  }




  /**
    * 发牌
    */
  override protected def deal(): Unit = {
    //测试的发牌
    if (this.roomPaijiu.isTest && this.roomPaijiu.getCurGameNumber % 2 == 0 && this.roomPaijiu.testUserId != 0) {
      val (maxCards, newCards) = PaijiuCardUtil.getMaxGroupAndNewCards(cards)
      val testPlayer = playerCardInfos(this.roomPaijiu.testUserId)
      testPlayer.cards = maxCards

      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", testPlayer.cards.asJava, this.roomPaijiu.testUserId)
      var slidList = newCards.sliding(4, 4).toList
      //两张牌模式
      if(Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)){
        slidList = cards.sliding(2, 2).toList
      }
      var count = 0


      for (playerInfo <- playerCardInfos.values if playerInfo.userId != this.roomPaijiu.testUserId) {
        playerInfo.cards ++= slidList(count)
        count += 1
        //发牌通知
        MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
      }

      //状态置回
      this.roomPaijiu.testUserId = 0
    } else {
      var slidList = cards.sliding(4, 4).toList
      if(Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)){
        slidList = cards.sliding(2, 2).toList
      }
      var count = 0
      for (playerInfo <- playerCardInfos.values) {
        playerInfo.cards ++= slidList(count)
        count += 1
        //发牌通知
        MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
      }
    }
  }


  def loadData(): Unit ={
//    var map = new util.HashMap[String,Double]()
//    map.put("bet",5)
//    map.put("rebate4",2)
//    map.put("rebate100",2.5)
//    map.put("pay_aa",3)
//    map.put("pay_one",5)
    this.roomPaijiu.rebateData = RedisManager.getConstantRedisService.getConstant
  }

  override protected def getGroupScoreByName(name: String): Int = {

    DataManager.data.getCrazyPaijiuCardGroupScoreDataMap.get(name)


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

    if(group == null || "".equals(group)) {
      println(group + " group null")
      return 0
    }
    val data = DataManager.data.getCrazyPaijiuCardGroupDataMap.get(group)
    if(data == null) {
      return 0
    }
    //没有这个牌型或者不含这个牌型
    if( getNoGroupName().contains(data.getName)) {
      //两张牌的点数相加

      getCrazyGroupScore(group, data)
    }else{
      getGroupScoreByName(data.getName)
    }

  }

  def getCrazyGroupScore(group:String, data : CrazyPaijiuCardGroupData):Int={

    if(!Room.isHasMode(MODE_GUIZI,roomPaijiu.getOtherMode) && data.getName.equals("ghost")) {
      return getGroupScoreByName("zero")
    }

    if(!Room.isHasMode(MODE_DIJIU,roomPaijiu.getOtherMode) && data.getName.equals("fieldninenn")) {
      return getGroupScoreByName("fieldone")
    }

    if(!Room.isHasMode(MODE_TIANJIU,roomPaijiu.getOtherMode) && data.getName.equals("skynineking")) {
      return getGroupScoreByName("skyone")
    }
    if(!Room.isHasMode(MODE_ZHADAN,roomPaijiu.getOtherMode) && data.getName.equals("boom")) {
      if(group.equals("5,32") || group.equals("32,5")) {
        return getGroupScoreByName("peopleone")
      }else{
        return getGroupScoreByName("mixone")
      }

    }
    return getGroupScoreByName(data.getName)
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
    var allCard = Array.concat(group1.split(",")).map(card => card.toInt).toList

    if(group2 != null && !"".equals(group2)) {
      allCard = Array.concat(group1.split(","), group2.split(",")).map(card => card.toInt).toList
    }

    //开的牌和拥有的牌相同
    val isSame = playerCardInfo.cards.diff(allCard).isEmpty
    if (!isSame) {
      logger.error("开的牌和拥有的不同")
      return false
    }

    //第一组不小于第二组牌
    val score1 = getGroupScore(group1)
    val score2 = getGroupScore(group2)
    if(score1<score2) {
      logger.error("第一组牌小")
    }
    score1 >= score2
  }


  def getNoGroupName(): Set[String] ={
    var set :Set[String] = Set()
    if(!Room.isHasMode(MODE_GUIZI,roomPaijiu.getOtherMode)) {
      set = set.+("ghost")
    }
    if(!Room.isHasMode(MODE_ZHADAN,roomPaijiu.getOtherMode)) {
      set = set.+("boom")
    }

    if(!Room.isHasMode(MODE_TIANJIU,roomPaijiu.getOtherMode)) {
      set = set.+("skynineking")
    }

    if(!Room.isHasMode(MODE_DIJIU,roomPaijiu.getOtherMode)) {
      set = set.+("fieldninenn")
    }
    set
  }


  /**
    * 转换为开牌状态
    */
  override def openStart(): Unit = {
    if(state != START_CRAP) {
      return
    }
    //发牌
    deal()
    state = STATE_OPEN
    //推送
    MsgSender.sendMsg2Player("gamePaijiuService", "openStart", this.bankerId, roomPaijiu.users)
    updateLastOperateTime()

  }


  /**
    * 摇骰子阶段
    */
  override def crapStart(): Unit = {
    if(this.state != STATE_BET) return
    MsgSender.sendMsg2Player("gamePaijiuService", "crapStart", 0, bankerId)
    this.state = START_CRAP
    updateLastOperateTime()
    //自动摇色子
    crap(this.bankerId)
  }

  /**
    * 牌局结束
    */
  override protected def gameOver(): Unit = {

    compute()
    sendResult()
    genRecord()
    //切庄开始

    this.lastOperateTime = System.currentTimeMillis

    if(isAutoBreakBanker()) {
      state = STATE_BANKER_BREAK
      bankerBreak(this.bankerId, true)
    }else{
      if(this.roomPaijiu.curGameNumber==1) {
//        updateLastOperateTime()
        this.roomPaijiu.clearReadyStatus(true)
//        this.roomPaijiu.startGame()
      }else{
        bankerBreakStart()
      }
    }
  }


  /**
    * 抢庄后选定庄家
    */
  override protected def chooseBankerAfterFight(): Unit = {
    //全选之后决定地主
    val isAllChoose = playerCardInfos.count { case (uid, playerInfo) => !playerInfo.isHasFightForBanker } == 0
    if (isAllChoose) {
      val wantTobeBankerList = playerCardInfos.filter { case (uid, playerInfo) => playerInfo.isFightForBanker }.toList
      //没人选择当庄家 则 创建者当庄家
      if (wantTobeBankerList.isEmpty) {
        roomPaijiu.setBankerId(roomPaijiu.users.get(0))
        this.bankerId = roomPaijiu.getBankerId
      } else {
        //随机选庄家
        val bid = new Random().shuffle(wantTobeBankerList).head._1
        roomPaijiu.setBankerId(bid)
        this.bankerId = bid
      }

      //通知玩家
      val map = Map("userId" -> this.bankerId)
      MsgSender.sendMsg2Player("gamePaijiuService", "chooseBanker", map.asJava, roomPaijiu.users)

      //庄家选分
//      bankerSetScoreStart()

      //扣钱
      RedisManager.getUserRedisService.addUserMoney(this.bankerId, -this.roomPaijiu.bankerScore)


      //开始下注
      betStart()

    }
  }

  /**
    * 是否所有人都开牌
    *
    * @return
    */
  override protected def isAllPlayerOpen(): Boolean = {
    playerCardInfos.count { case (uid, playerInfo) => playerInfo.group1 == null } == 0
  }






  /**
    * 最终结算版
    */
  override protected def sendFinalResult(): Unit ={

    val userOfResultList = this.roomPaijiu.getUserOfResult
    // 存储返回
    val gameOfResult = new GameOfResult

    //庄家初始分 再减掉
    //todo 庄家的分如何处理
//    roomPaijiu.addUserSocre(this.roomPaijiu.getBankerId, -this.roomPaijiu.bankerInitScore)

    //战绩
    this.roomPaijiu.genRoomRecord()

    //大赢家付房费


    if (!this.roomPaijiu.isAA && !this.roomPaijiu.isInstanceOf[RoomPaijiu100]) {
      //找到大赢家
      val winner = this.roomPaijiu.getMaxScoreUser
      val money = this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_PAY_ONE).asInstanceOf[String].toDouble
      //付房费
      RedisManager.getUserRedisService.addUserMoney(winner, -money)
      gameOfResult.setOther(Map("isAA"->this.roomPaijiu.isAA,"winnerId"->winner, "cost"->money).asJava)
    }else{
      gameOfResult.setOther(Map("isAA"->this.roomPaijiu.isAA, "cost"->this.roomPaijiu.getNeedMoney).asJava)
    }

    //返利
    if(!this.roomPaijiu.isInstanceOf[RoomPaijiu100]) {

      val rebate:Double = this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_REBATE4).asInstanceOf[String].toDouble
      for(playerInfo <- this.playerCardInfos.values){
        this.roomPaijiu.sendCenterAddRebate(playerInfo.userId, rebate)
      }
    }


    gameOfResult.setUserList(userOfResultList)
    MsgSender.sendMsg2Player("gameService", "gamePaijiuFinalResult", gameOfResult, roomPaijiu.users)
    RoomManager.removeRoom(roomPaijiu.getRoomId)


    //重开一个一样的房间
    if (this.roomPaijiu.isReOpen) {
      doCreateNewRoom(this.roomPaijiu)
    }
  }

  /**
    * 发送建房请求
    * @param room
    */
  def doCreateNewRoom(room:RoomPaijiu): Unit ={
    var personNum = 4
    if(this.isInstanceOf[GamePaijiu100]) {
      personNum = room.getPersonNumber
    }
    RoomPaijiuCrazy.createRoom(room.getCreateUser,room.getRoomType, room.getGameType, room.getGameNumber, room.getClubId, room.getClubRoomModel,room.getClubMode,
      room.isAA,room.robotType, room.robotNum, room.robotWinner,room.isReOpen, room.getOtherMode, personNum,room.bankerInitScore )
  }



  def isAutoBreakBanker():Boolean ={
    //大于10倍 小于20% 自动切庄
    if(this.roomPaijiu.bankerScore > this.roomPaijiu.bankerInitScore * 10 || this.roomPaijiu.bankerScore<=0) return true
    for(playerInfo <- this.playerCardInfos.values){
      if(this.bankerId != playerInfo.userId){
        if(RedisManager.getUserRedisService.getUserMoney(playerInfo.userId) <=0){
          return true
        }
      }
    }
    return false
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
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreakResp", Map("userId"->userId, "flag"->flag).asJava, this.roomPaijiu.getUsers)

    if (flag) {
      //换庄家
      //把钱加到庄身上


      //换庄家
      //把钱加到庄身上
      //抽水
      val winScore:Double = this.roomPaijiu.bankerScore - this.roomPaijiu.bankerInitScore
      var rebate:Double = 0
//      if(winScore > 0) {
//        //返利
//        rebate =  winScore * this.roomPaijiu.rebateData.get(IGameConstant.PAIJIU_REBATE4).asInstanceOf[String].toDouble / 100
//        this.roomPaijiu.sendCenterAddRebate(userId, rebate)
//
//      }else{
//        rebate = 0
//      }

      RedisManager.getUserRedisService.addUserMoney(bankerId,this.roomPaijiu.bankerScore - rebate)



//      this.roomPaijiu.lastBankerInitScore = this.roomPaijiu.bankerInitScore
      this.roomPaijiu.setBankerId(0)
      this.roomPaijiu.bankerScore = 0
      this.roomPaijiu.clearReadyStatus(true)
      sendFinalResult()

    } else {
      this.roomPaijiu.clearReadyStatus(true)
//      this.roomPaijiu.startGame()
    }

    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreak", 0, userId)
    0
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
    if (playerCardInfoPaijiu.bet != null) return ErrorCode.ALREADY_BET
    //下注不合法

    val bet = new Bet(one, two,three,index)
    if (!checkBet(bet)) return ErrorCode.BET_PARAM_ERROR
    //金币牌九 下注不能大于身上的钱
    val betNum:Int = one + two + three
    val myMoney = RedisManager.getUserRedisService.getUserMoney(userId)
    if(myMoney<betNum) {
      return ErrorCode.BET_PARAM_NO_MONEY
    }

    playerCardInfoPaijiu.bet = bet

//    this.roomPaijiu.addUserSocre(userId, -betNum)


    val result = Map("userId" -> userId, "bet" -> bet)
    MsgSender.sendMsg2Player("gamePaijiuService", "betResult", result.asJava, users)
    MsgSender.sendMsg2Player("gamePaijiuService", "bet", 0, userId)

    //除去庄家全都下完注
    val count = playerCardInfos.count { case (uid, playerInfo) => uid != bankerId && playerInfo.bet != null }
    val isAllBet = count == users.size() - 1
    if (isAllBet) {
      crapStart()
    }
    0
  }


  /**
    * 记录胜负平日志
    */
  def dataLog(): Unit ={
    val oneId = nextTurnId(this.bankerId)
    val gamePaijiuResult = new GamePaijiuResult()

    doLogRecord(gamePaijiuResult, 1, getSFP(playerCardInfos(oneId).getScore()))
    if(this.users.size>2){
      val twoId = nextTurnId(oneId)
      doLogRecord(gamePaijiuResult, 2, getSFP(playerCardInfos(twoId).getScore()))
      if(this.users.size()>3){
        val threeId = nextTurnId(twoId)
        doLogRecord(gamePaijiuResult, 3, getSFP(playerCardInfos(threeId).getScore()))
      }
    }

    this.roomPaijiu.winnerIndex.append(gamePaijiuResult)

    if(this.roomPaijiu.winnerIndex.size>10){
      this.roomPaijiu.winnerIndex.remove(0)
    }
  }


  /**
    * 记录
    * @param gamePaijiuResult
    * @param index
    * @param sfp
    */
  def doLogRecord(gamePaijiuResult:GamePaijiuResult,index:Int, sfp:Int): Unit ={
    if(index == 1){
      gamePaijiuResult.setOne(sfp)
      val count = this.roomPaijiu.winnerCountMap.getOrDefault(1,0)
      this.roomPaijiu.winnerCountMap.put(1, count + 1)
    }
    if(index == 2) {
      gamePaijiuResult.setTwo(sfp)
      val count = this.roomPaijiu.winnerCountMap.getOrDefault(2,0)
      this.roomPaijiu.winnerCountMap.put(2, count + 1)
    }
    if(index == 3) {
      gamePaijiuResult.setThree(sfp)
      val count = this.roomPaijiu.winnerCountMap.getOrDefault(3,0)
      this.roomPaijiu.winnerCountMap.put(3, count + 1)
    }
  }


  /**
    * 获得胜负平
    * @param score
    * @return
    */
  def getSFP(score:Double):Int={
    if(score>0){
      return 1
    }else if(score<0){
      return -1
    }else{
      return 0
    }
  }

  /**
    * 结算
    */
  override def compute(): Unit = {
    val banker = playerCardInfos(bankerId)
    var winUsers: List[PlayerCardInfoPaijiu] = List()
    val mix8Score = getGroupScoreByName(MIX_8)
    val sky8Score = getGroupScoreByName(SKY_8)
    var resultSet: Set[Int] = Set()
    val bankerScore1 = getGroupScore(banker.group1)
    val bankerScore2 = getGroupScore(banker.group2)
    println("bankerscore1: " + bankerScore1)
    playerCardInfos.foreach { case (uid, other) =>
      if (uid != bankerId && other.bet != null) {
        val otherScore1 = getGroupScore(other.group1)
        println("otherscore1: " + otherScore1)

        val otherScore2 = getGroupScore(other.group2)
        var result: Int = 0
        if (bankerScore1 >= otherScore1) result += 1
        if (bankerScore1 < otherScore1) result -= 1
        //四张的牌九
        if(!Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)){
          if (bankerScore2 >= otherScore2) result += 1
          if (bankerScore2 < otherScore2) result -= 1
        }
        resultSet = resultSet.+(result)
        //庄家赢
        if (result > 0) {
          val isHas3Bet = Room.isHasMode(MODE_BET_3, this.roomPaijiu.otherMode)
          var changeScore = other.getBetScore(bankerScore2 >= mix8Score, isHas3Bet && bankerScore2>sky8Score)
          //两张牌也判断赢三道
          if(Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)) {
            changeScore = other.getBetScore(bankerScore1 >= mix8Score, isHas3Bet && bankerScore1>sky8Score)
          }
          banker.addScore(roomPaijiu,changeScore)
          other.addScore(roomPaijiu,-changeScore)

          roomPaijiu.bankerScore += changeScore
          //庄的分最后再加
//          roomPaijiu.addUserSocre(banker.userId, changeScore)
          roomPaijiu.addUserSocre(other.userId, -changeScore)
          roomPaijiu.logRoomStatistics(other.userId, -changeScore)
          roomPaijiu.logRoomStatistics(banker.userId, changeScore)
          other.winState = LOSE

          logger.info("庄家赢得钱: " + changeScore)



        } else if (result < 0) {
          other.winState = WIN
          winUsers = winUsers.+:(other)
        }else{
          logger.info("和了")
        }

      }
    }

    //记录 胜负平记录
    dataLog()

    //全赢或全输
    if (resultSet.size == 1) {
      val bankerStatiseics = this.roomPaijiu.getRoomStatisticsMap.get(bankerId)
      if (resultSet.contains(WIN)) bankerStatiseics.winAllTime += 1
      if (resultSet.contains(LOSE)) bankerStatiseics.loseAllTime += 1
    }

    //排序后的
    val sortedUsers = winUsers.sortWith(compareByScore)
    for (playerInfo <- sortedUsers) {
      val score1 = getGroupScore(playerInfo.group1)
      val score2 = getGroupScore(playerInfo.group2)
      //庄家应该输的钱
      val isHas3Bet = Room.isHasMode(MODE_BET_3, this.roomPaijiu.otherMode)
      var bankerLoseScore = playerInfo.getBetScore(score2 >= mix8Score, isHas3Bet && bankerScore2>sky8Score)
      //两张牌也判断赢三道
      if(Room.isHasMode(MODE_2CARD,this.roomPaijiu.otherMode)) {
        bankerLoseScore = playerInfo.getBetScore(score1 >= mix8Score, isHas3Bet && score1>sky8Score)
      }

      val loseScore = if (bankerLoseScore > roomPaijiu.bankerScore) roomPaijiu.bankerScore else bankerLoseScore
      logger.info("应输的钱: " + bankerLoseScore)
      logger.info("实际的钱: " + loseScore)
      logger.info("庄家的钱: " + banker.score)

      //分数变化
      banker.addScore(roomPaijiu, -loseScore.toInt)
      roomPaijiu.bankerScore -= loseScore.toInt
      playerInfo.addScore(roomPaijiu, loseScore.toInt)
//      roomPaijiu.addUserSocre(banker.userId, -loseScore)
      roomPaijiu.addUserSocre(playerInfo.userId, loseScore)

      roomPaijiu.logRoomStatistics(playerInfo.userId, loseScore)
      roomPaijiu.logRoomStatistics(banker.userId, -loseScore)

    }
  }

  /**
    * 排序
    *
    * @param playerInfo1
    * @param playerInfo2
    * @return
    */
  def compareByScore(playerInfo1: PlayerCardInfoPaijiu, playerInfo2: PlayerCardInfoPaijiu): Boolean = {
    val playerScore1 = getCardScore(playerInfo1)
    val playerScore2 = getCardScore(playerInfo2)

    if (playerScore1._2 > playerScore2._2) {
      true
    } else if (playerScore1._2 == playerScore2._2) {
      if (playerScore1._1 > playerScore2._1) true else false
    } else {
      false
    }
  }

}
