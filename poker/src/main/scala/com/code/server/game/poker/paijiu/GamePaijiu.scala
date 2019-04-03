package com.code.server.game.poker.paijiu


import java.lang.Long
import java.util

import com.code.server.constant.data.{DataManager, StaticDataProto}
import com.code.server.constant.response._
import com.code.server.game.room.kafka.MsgSender
import com.code.server.game.room.service.RoomManager
import com.code.server.game.room.{Game, Room}
import com.code.server.redis.service.RedisManager
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
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

  var catchBanker : Boolean = false

  var commonCards:Map[Int,List[Int]] = Map()

  var isRobotBet:Boolean = false





  //  var room: RoomPaijiu = _
  def setCatchBankerTrue(): Unit ={
    catchBanker = true;
  }

  /**
    * 开始游戏
    *
    * @param users
    * @param room
    */
  override def startGame(users: util.List[Long], room: Room): Unit = {
    roomPaijiu = room.asInstanceOf[RoomPaijiu]
    //    this.room = roomPaijiu
    //实例化玩家
    initPlayer()
    //码牌
    initCards()

    //轮庄
    val changeStep = roomPaijiu.getGameNumber / roomPaijiu.getUsers.size()
    if (roomPaijiu.getCurGameNumber != 1 && (roomPaijiu.getCurGameNumber - 1) % changeStep == 0) {
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
  protected def initPlayer(): Unit = {
    this.users.addAll(roomPaijiu.getUsers)
    roomPaijiu.getUsers.forEach(uid => {
      val playerInfo = new PlayerCardInfoPaijiu
      playerInfo.userId = uid
      this.playerCardInfos += (uid -> playerInfo)
    })
  }

  protected def initCards(): Unit = {
    //两局中的第一局 单数局
    val isFirst = roomPaijiu.getCurGameNumber % 2 == 1
    //单数局
    if (isFirst) {
      //初始牌并洗牌
      val cardList = DataManager.data.getPaijiuCardDataMap.values().asScala.map(card => card.getCard).toList
      val rand = new Random
      roomPaijiu.cards = rand.shuffle(cardList)

      //拿出一半的牌
      val thisGameCards = roomPaijiu.cards.slice(0, 16)
      //本局的牌
      cards ++= thisGameCards
      //room中的牌删除本局的牌
      roomPaijiu.cards = roomPaijiu.cards.diff(thisGameCards)
      roomPaijiu.lastGameCards = thisGameCards
    } else {
      //双数局
      cards ++= roomPaijiu.cards.slice(0, 16)
    }
  }

  /**
    * 发牌
    */
  protected def deal(): Unit = {
    //测试的发牌
    if (this.roomPaijiu.isTest && this.roomPaijiu.getCurGameNumber % 2 == 0 && this.roomPaijiu.testUserId != 0) {
      val (maxCards, newCards) = PaijiuCardUtil.getMaxGroupAndNewCards(cards)
      val testPlayer = playerCardInfos(this.roomPaijiu.testUserId)
      testPlayer.cards = maxCards

      MsgSender.sendMsg2Player("gamePaijiuService", "getCards", testPlayer.cards.asJava, this.roomPaijiu.testUserId)
      val slidList = newCards.sliding(4, 4).toList
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
      val slidList = cards.sliding(4, 4).toList
      var count = 0
      for (playerInfo <- playerCardInfos.values) {
        playerInfo.cards ++= slidList(count)
        count += 1
        //发牌通知
        MsgSender.sendMsg2Player("gamePaijiuService", "getCards", playerInfo.cards.asJava, playerInfo.userId)
      }
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
  def bet(userId: Long, one: Int, two: Int,three : Int, index:Int): Int = {
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
    if (this.roomPaijiu.isInstanceOf[RoomPaijiuAce]|| this.roomPaijiu.isInstanceOf[RoomPaijiuCrazy]){
      val myMoney = RedisManager.getUserRedisService.getUserMoney(userId)
      if(myMoney<one + two + three) {
        return ErrorCode.BET_PARAM_ERROR
      }
    }

    playerCardInfoPaijiu.bet = bet


    val result = Map("userId" -> userId, "bet" -> bet)
    MsgSender.sendMsg2Player("gamePaijiuService", "betResult", result.asJava, roomPaijiu.users)
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
    * 摇骰子阶段
    */
  protected def crapStart(): Unit = {
    MsgSender.sendMsg2Player("gamePaijiuService", "crapStart", 0, bankerId)
    this.state = START_CRAP
    updateLastOperateTime()
  }


  /**
    * 验证下注
    *
    * @param bet
    * @return
    */
  protected def checkBet(bet: Bet): Boolean = {
    bet.one > 0 && bet.two >= 0
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
  protected def isAllPlayerOpen(): Boolean = {
    playerCardInfos.count { case (uid, playerInfo) => playerInfo.group1 == null && playerInfo.group2 == null } == 0
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
    if (!isSame) return false

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
    genRecord()
    this.roomPaijiu.clearReadyStatus(true)
    sendFinalResult()

  }


  /**
    * 生成战绩
    */
  override protected def genRecord(): Unit = {

    import com.code.server.util.IdWorker
    val id = IdWorker.getDefaultInstance.nextId
    val map = new util.HashMap[Long, java.lang.Double]()
    for (playerInfo <- playerCardInfos) {
      map.put(playerInfo._2.userId, playerInfo._2.score)
    }
    genRecord(map, this.roomPaijiu, id)
    //    genRecord(playerCardInfos.values.toMap((playerInfo)=>))
  }


  /**
    * 生成战绩
    */
  protected def genRecordForGold(): Unit = {

    import com.code.server.util.IdWorker
    val id = IdWorker.getDefaultInstance.nextId
    val map = new util.HashMap[Long, java.lang.Double]()
    var temp = 0.0;
    for (playerInfo <- playerCardInfos) {
      if(playerInfo._2.userId!=bankerId){
        map.put(playerInfo._2.userId,playerInfo._2.score);
        temp+= playerInfo._2.score;
      }
    }
    map.put(bankerId,-temp);
    genRecord(map, this.roomPaijiu, id)
    //    genRecord(playerCardInfos.values.toMap((playerInfo)=>))
  }


  /**
    * 结算
    */
  protected def compute(): Unit = {
    println("结算")
    val banker = playerCardInfos(bankerId)
    var resultSet: Set[Int] = Set()
    playerCardInfos.foreach { case (uid, playerInfo) =>
      if (uid != bankerId) {
        val winResult: Int = compareAndSetScore(banker, playerInfo)
        resultSet = resultSet.+(winResult)
      }
    }
    //全赢或全输
    if (resultSet.size == 1) {
      val bankerStatiseics = this.roomPaijiu.getRoomStatisticsMap.get(bankerId)
      if (resultSet.contains(WIN)) bankerStatiseics.winAllTime += 1
      if (resultSet.contains(LOSE)) bankerStatiseics.loseAllTime += 1
    }
  }

  /**
    * 牌局结果
    */
  protected def sendResult(): Unit = {
    var gameResult = new GameResultPaijiu
    this.playerCardInfos.values.foreach(playerInfo => gameResult.getPlayerCardInfos.add(playerInfo.toVo))
    MsgSender.sendMsg2Player("gamePaijiuService", "gameResult", gameResult, roomPaijiu.users)
  }




  /**
    * 最终结算版
    */
  protected def sendFinalResult(): Unit = {

    if (this.roomPaijiu.isRoomOver()) {
      val userOfResultList = this.roomPaijiu.getUserOfResult
      // 存储返回
      val gameOfResult = new GameOfResult
      gameOfResult.setUserList(userOfResultList)
      MsgSender.sendMsg2Player("gameService", "gamePaijiuFinalResult", gameOfResult, roomPaijiu.users)
      RoomManager.removeRoom(roomPaijiu.getRoomId)

      //庄家初始分 再减掉
      roomPaijiu.addUserSocre(this.roomPaijiu.getBankerId, -this.roomPaijiu.bankerInitScore)

      //战绩
      this.roomPaijiu.genRoomRecord()
    }
  }


  /**
    * 比较输赢并设置分数
    *
    * @param banker
    * @param other
    */
  protected def compareAndSetScore(banker: PlayerCardInfoPaijiu, other: PlayerCardInfoPaijiu): Int = {
    val mix8Score = getGroupScoreByName(MIX_8)
    if(banker.group1 == null) {
      println("null")
    }
    val bankerScore1 = getGroupScore(banker.group1)
    val bankerScore2 = getGroupScore(banker.group2)
    val otherScore1 = getGroupScore(other.group1)
    val otherScore2 = getGroupScore(other.group2)
    var result: Int = 0
    if (bankerScore1 >= otherScore1) result += 1
    if (bankerScore1 < otherScore1) result -= 1
    if (bankerScore2 >= otherScore2) result += 1
    if (bankerScore2 < otherScore2) result -= 1
    //庄家赢
    if (result > 0) {
      val changeScore = other.getBetScore(bankerScore2 >= mix8Score)
      banker.addScore(this.roomPaijiu, changeScore)
      other.addScore(this.roomPaijiu, -changeScore)
      roomPaijiu.addUserSocre(banker.userId, changeScore)
      roomPaijiu.addUserSocre(other.userId, -changeScore)
      other.winState = LOSE
    } else if (result < 0) {
      //闲家赢
      val changeScore = other.getBetScore(otherScore2 >= mix8Score)
      banker.addScore(this.roomPaijiu, -changeScore)
      other.addScore(this.roomPaijiu, changeScore)
      roomPaijiu.addUserSocre(banker.userId, -changeScore)
      roomPaijiu.addUserSocre(other.userId, changeScore)
      other.winState = WIN
    }
    result
  }

  /**
    * 获得牌型分数
    *
    * @param group
    * @return
    */
  def getGroupScore(group: String): Int = {

    val d: StaticDataProto.DataManager = DataManager.data
    val dataStr = DataManager.data.getRoomDataMap.get(this.roomPaijiu.getGameType).getPaijiuDataName
    val dataMethodName = "get" + dataStr + "GroupDataMap"
    val method = d.getClass.getDeclaredMethod(dataMethodName)
    val m = method.invoke(d)
    val mp = m.asInstanceOf[java.util.Map[String,Object]]
    val o = mp.get(group)
    val nameMethod = o.getClass.getDeclaredMethod("getName")
    val name = nameMethod.invoke(o)
//    name.asInstanceOf[String]
    getGroupScoreByName(name.asInstanceOf[String])


//    val name: String = DataManager.data.getPaijiuCardGroupDataMap.get(group).getName
//    logger.info("cardgroupName : " + name)
//    DataManager.data.getPaijiuCardGroupScoreDataMap.get(name).getScore
  }
  /**
    * 获取牌得分
    *
    * @param playerInfo
    * @return
    */
  protected def getCardScore(playerInfo: PlayerCardInfoPaijiu): (Int, Int) = {
    val score1 = getGroupScore(playerInfo.group1)
    val score2 = getGroupScore(playerInfo.group2)
    (score1, score2)
  }

  /**
    * 交换(测试用)
    *
    * @param userId
    */
  def exchange(userId: Long): Int = {
    //单数局
    if (this.roomPaijiu.getCurGameNumber % 2 != 0) {
      val player = playerCardInfos(userId)
      val oldCards = player.cards
      val allCards = this.roomPaijiu.cards ++ player.cards
      val (maxGroup, newCards) = PaijiuCardUtil.getMaxGroupAndNewCards(allCards)

      //一副新牌 重新洗
      var cardList = DataManager.data.getPaijiuCardDataMap.values().asScala.map(card => card.getCard).toList
      val rand = new Random
      cardList = rand.shuffle(cardList)
      this.roomPaijiu.cards = newCards
      this.roomPaijiu.lastGameCards = cardList.diff(newCards)
      player.cards = maxGroup
      MsgSender.sendMsg2Player("gamePaijiuService", "exchange", Map("cards" -> player.cards.asJava).asJava, this.users)
      0
    } else {

      ErrorCode.BET_PARAM_ERROR
    }
  }

  /**
    * 设置测试的id
    *
    * @param userId
    */
  def setTestUser(userId: Long): Int = {
    this.roomPaijiu.testUserId = userId
    MsgSender.sendMsg2Player("gamePaijiuService", "setTestUser", 0, this.users)
    0
  }

  def setBankerId(userId: Long): Unit = {
    this.bankerId = userId
  }

  protected def getGroupScoreByName(name: String): Int = {
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
    * 转换为下注状态
    */
  protected def betStart(): Unit = {
    state = STATE_BET

    val param = Map("bankerId" -> this.bankerId, "curGameNumber" -> this.roomPaijiu.getCurGameNumber)
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "betStart", param.asJava, roomPaijiu.users)
    //双数局 通知上把牌
    if (roomPaijiu.getCurGameNumber % 2 == 0) {
      MsgSender.sendMsg2Player("gamePaijiuService", "lastCards", roomPaijiu.lastGameCards.asJava, roomPaijiu.users)
    }

    updateLastOperateTime()
  }

  /**
    * 抢庄状态
    */
  protected def fightForBankerStart(): Unit = {
    state = STATE_FIGHT_FOR_BANKER
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "fightForBankerStart", this.bankerId, roomPaijiu.users)
  }

  /**
    * 庄家设置分数状态
    */
  def bankerSetScoreStart(): Unit = {
    state = STATE_BANKER_SET_SCORE
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerSetScoreStart", this.bankerId, roomPaijiu.users)
  }

  /**
    * 切状状态
    */
  protected def bankerBreakStart(): Unit = {
    state = STATE_BANKER_BREAK
    //推送开始下注
    val result = Map("bankerId" -> this.bankerId)
    updateLastOperateTime()
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreakStart", result.asJava, roomPaijiu.users)
  }

  /**
    * 转换为开牌状态
    */
  def openStart(): Unit = {
    //发牌
    deal()
    state = STATE_OPEN
    updateLastOperateTime()
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "openStart", this.bankerId, roomPaijiu.users)
  }

  /**
    * 掷骰子
    */
  def crap(userId: Long): Int = {
    if (state != START_CRAP) return ErrorCode.CRAP_PARAM_ERROR
    if (userId != bankerId) return ErrorCode.NOT_BANKER

    val rand = new Random()
    val num1 = rand.nextInt(6) + 1
    val num2 = rand.nextInt(6) + 1
    val result = Map("num1" -> num1, "num2" -> num2)
    MsgSender.sendMsg2Player("gamePaijiuService", "randSZ", result.asJava, roomPaijiu.users)
    MsgSender.sendMsg2Player("gamePaijiuService", "crap", 0, userId)
    openStart()
    0
  }

  /**
    * 抢庄
    *
    * @param userId
    * @param flag
    * @return
    */
  def fightForBanker(userId: Long, flag: Boolean): Int = {
    val playerCardInfoPaijiuOption = playerCardInfos.get(userId)
    if (playerCardInfoPaijiuOption.isEmpty) return ErrorCode.NO_USER
    val playerCardInfoPaijiu = playerCardInfoPaijiuOption.get

    if (playerCardInfoPaijiu.isHasFightForBanker) return ErrorCode.ALREADY_FIGHT_FOR_BANKER

    playerCardInfoPaijiu.isFightForBanker = flag
    playerCardInfoPaijiu.isHasFightForBanker = true

    val map = Map("userId" -> userId, "flag" -> flag)
    MsgSender.sendMsg2Player("gamePaijiuService", "fightForBankerResult", map.asJava, roomPaijiu.users)
    MsgSender.sendMsg2Player("gamePaijiuService", "fightForBanker", 0, userId)

    //选定庄家
    if(bankerId != -1L) chooseBankerAfterFight()

    0
  }



  /**
    * 庄家设置分
    *
    * @param userId
    * @param score
    * @return
    */
  def bankerSetScore(userId: Long, score: Int): Int = {
    if (userId != bankerId) return ErrorCode.NOT_BANKER
    if (roomPaijiu.getCurGameNumber != 1) return ErrorCode.BANKER_SET_SCORE_GAMENUM_ERROR
    roomPaijiu.bankerScore = score
    roomPaijiu.bankerInitScore = score
    val result = Map("score" -> score)
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerSetScoreResult", result.asJava, roomPaijiu.users)
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerSetScore", 0, userId)
    //下注
    betStart()
    0
  }

  /**
    * 庄家切庄(牌局结束)
    *
    * @param userId
    * @return
    */
  def bankerBreak(userId: Long, flag: Boolean): Int = {
    if (userId != bankerId) return ErrorCode.NOT_BANKER
    if (roomPaijiu.getCurGameNumber < 3) return ErrorCode.BANKER_BREAK_GAMENUM_ERROR

    if (flag) {
      //结束
      sendFinalResult()
    } else {
      betStart()
    }
    MsgSender.sendMsg2Player("gamePaijiuService", "bankerBreak", 0, userId)
    0
  }


  def addUser(userId:Long, playerInfo:PlayerCardInfoPaijiu): Unit ={
    this.users.add(userId)
    this.playerCardInfos += (userId -> playerInfo)
  }


  /**
    * 抢庄后选定庄家
    */
  protected def chooseBankerAfterFight(): Unit = {
    //全选之后决定地主
    val isAllChoose = playerCardInfos.count { case (uid, playerInfo) => !playerInfo.isHasFightForBanker } == 0
    if (isAllChoose) {
      val wantTobeBankerList = playerCardInfos.filter { case (uid, playerInfo) => playerInfo.isFightForBanker }.toList
      //没人选择当庄家 则 创建者当庄家
      if (wantTobeBankerList.isEmpty) {
        roomPaijiu.setBankerId(roomPaijiu.getBankerId)
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
      bankerSetScoreStart()
    }
  }

  /**
    * 抢庄后选定庄家
    */
  protected def chooseBankerAfterFightForGold(): Unit = {
    //全选之后决定地主
    val isAllChoose = playerCardInfos.count { case (uid, playerInfo) => !playerInfo.isHasFightForBanker } == 0
    if (isAllChoose) {
      val wantTobeBankerList = playerCardInfos.filter { case (uid, playerInfo) => playerInfo.isFightForBanker }.toList
      //没人选择当庄家 则 创建者当庄家
      if (wantTobeBankerList.isEmpty) {
        roomPaijiu.setBankerId(roomPaijiu.getBankerId)
        this.bankerId = roomPaijiu.getBankerId
      } else {
        //随机选庄家
        if(!catchBanker){
          val bid = new Random().shuffle(wantTobeBankerList).head._1
          roomPaijiu.setBankerId(bid)
          this.bankerId = bid
        }
      }
      //通知玩家
      val map = Map("userId" -> this.bankerId)
      MsgSender.sendMsg2Player("gamePaijiuService", "chooseBanker", map.asJava, roomPaijiu.users)

      //庄家选分
      bankerSetScoreStart()
    }
  }

  /**
    * 获得牌型的最大分数
    *
    * @param cards
    * @return
    */
  def getCardsMaxScore(cards: List[Int]): (Int,String) = {

    var cardList:List[String] = List()
    if (cards.size == 2) {
      cardList = cardList.+:(cards.head + "," + cards(1))
      cardList = cardList.+:(cards(1) + "," + cards.head)
    } else  {
      cardList = cardList:::getGroupList(cards)
    }
    var max = 0
    var group = ""
    for (x <- cardList) {
      val score = getGroupScore(x)
      if (score > max) {
        max = score
        group = x
      }
    }
    (max,group)
  }



  def getMaxOpenGroup(cards:List[Int]):(String,String) = {
    val group1 = getCardsMaxScore(cards)._2
    if(cards.size == 2) {
      (group1, null)
    }else{
      val cl = group1.split(",")
      var cs:ListBuffer[Int] = ListBuffer()
      cs.++=(cards)
      for(c<-cl){
        cs -= c.toInt
      }
      val group2 = getCardsMaxScore(cs.toList)._2
      (group1,group2)
    }
  }

  /**
    * 获得默认牌组
    * @param cards
    * @return
    */
  def getCardsDefaultGroup(cards:List[Int]):(String,String) = {
    var g1 = ""
    var g2 = ""
    if(cards.size == 2){
      g1 = cards.head + "," + cards(1)
    }else{
      g1 = cards.head + "," + cards(1)
      g2 = cards(2) + "," + cards(3)
    }
    (g1, g2)
  }

  /**
    * 获得牌型组合
    *
    * @param cards
    * @return
    */
  def getGroupList(cards: List[Int]): List[String] = {
    var list: List[String] = List(
      cards(0) + "," + cards(1),
      cards(1) + "," + cards(0),
      cards(0) + "," + cards(2),
      cards(2) + "," + cards(0),
      cards(0) + "," + cards(3),
      cards(3) + "," + cards(0),
      cards(1) + "," + cards(2),
      cards(2) + "," + cards(1),
      cards(1) + "," + cards(3),
      cards(3) + "," + cards(1),
      cards(2) + "," + cards(3),
      cards(3) + "," + cards(2)
    )
    list
  }

  override def toVo(watchUser: scala.Long): IfaceGameVo = {
    val gamePaijiuVo = new GamePaijiuVo
    gamePaijiuVo.bankerId = this.bankerId
    gamePaijiuVo.state = this.state
    gamePaijiuVo.bankerInitScore = roomPaijiu.bankerInitScore
    this.playerCardInfos.foreach { case (userId, playerInfo) =>
      gamePaijiuVo.playerCardInfos.put(userId, playerInfo.toVo(watchUser))
    }
    //双数局
    if (this.roomPaijiu.getCurGameNumber % 2 == 0) {
      gamePaijiuVo.lastCards = this.roomPaijiu.lastGameCards.asJava
    }
    gamePaijiuVo

  }


}

