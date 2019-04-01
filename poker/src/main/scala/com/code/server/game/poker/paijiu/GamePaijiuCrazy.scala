package com.code.server.game.poker.paijiu

import java.{lang, util}

import com.code.server.constant.data.{DataManager, StaticDataProto}
import com.code.server.constant.kafka.{IKafaTopic, IkafkaMsgId, KafkaMsgKey}
import com.code.server.constant.response.ErrorCode
import com.code.server.game.room.Room
import com.code.server.game.room.kafka.MsgSender
import com.code.server.kafka.MsgProducer
import com.code.server.redis.service.RedisManager
import com.code.server.util.SpringUtil
import scala.collection.JavaConverters._
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
      loadData()
      roomPaijiu = room.asInstanceOf[RoomPaijiu]
      //实例化玩家
      initPlayer()
      //码牌
      initCards()

      bankerId = roomPaijiu.getBankerId

      room.getCurGameNumber match {
        case 1 => {
          fightForBankerStart()
        }
        case _ => betStart()
      }
  }

  def loadData(): Unit ={
    this.rebateData = RedisManager.getConstantRedisService.getConstant
    println(this.rebateData)
  }

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
      if(group == null) {
        println("group null")
      }
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
    * 转换为开牌状态
    */
  override def openStart(): Unit = {
    //发牌
    deal()
    state = STATE_OPEN
    updateLastOperateTime()
    //推送开始下注
    MsgSender.sendMsg2Player("gamePaijiuService", "openStart", this.bankerId, users)

  }


  /**
    * 摇骰子阶段
    */
  override protected def crapStart(): Unit = {
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

    updateLastOperateTime()
    if(isAutoBreakBanker()) {
      bankerBreak(this.bankerId, true)
    }else{
      if(this.roomPaijiu.curGameNumber==1) {
        this.roomPaijiu.clearReadyStatus(true)
        this.roomPaijiu.startGame()
      }else{
        bankerBreakStart()
      }
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


  def sendCenterAddMoney(userId:Long,money:Double): Unit ={
    val addMoney = Map("userId"->userId, "money"->money)
    val kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_ADD_MONEY)
    val msgProducer = SpringUtil.getBean(classOf[MsgProducer])
    msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, addMoney.asJava)
  }

  def sendCenterAddRebate(userId:Long, money:Double): Unit ={
    val addMoney = Map("userId"->userId, "money"->money)
    val kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_ADD_REBATE)
    val msgProducer = SpringUtil.getBean(classOf[MsgProducer])
    msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, addMoney.asJava)
  }


  /**
    * 最终结算版
    */
  override protected def sendFinalResult(): Unit ={

   super.sendFinalResult()

    //大赢家付房费
    if(this.roomPaijiu.isRoomOver ) {

      if(Room.isHasMode(MODE_WINNER_PAY, this.roomPaijiu.getOtherMode)) {
        //找到大赢家
        val winner = this.roomPaijiu.getMaxScoreUser
        //付房费
        RedisManager.getUserRedisService.addUserMoney(winner, -this.roomPaijiu.getNeedMoney)
      }
      //重开一个一样的房间
      if(this.roomPaijiu.isReOpen) {
        doCreateNewRoom(this.roomPaijiu)
      }
    }


  }

  /**
    * 发送建房请求
    * @param room
    */
  def doCreateNewRoom(room:RoomPaijiu): Unit ={
    RoomPaijiuCrazy.createRoom(0,room.getRoomType, room.getGameType, room.getGameNumber, room.getClubId, room.getClubRoomModel,room.getClubMode,
      room.isAA,room.robotType, room.robotNum, room.robotWinner,room.isReOpen, room.getOtherMode, room.getPersonNumber)
  }



  def isAutoBreakBanker():Boolean ={
    //大于10倍 小于20% 自动切庄
    if(this.roomPaijiu.bankerScore > this.roomPaijiu.bankerInitScore * 10 || this.roomPaijiu.bankerScore<=0) true
    for(playerInfo <- this.playerCardInfos.values){
      if(this.bankerId != playerInfo.userId){
        if(RedisManager.getUserRedisService.getUserMoney(playerInfo.userId) <=0){
          true
        }
      }
    }
    false
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
