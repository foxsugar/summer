package com.code.server.game.poker.paijiu


import java.lang.Long
import java.util.Collections

import com.code.server.constant.kafka.KafkaMsgKey
import com.code.server.constant.response.ResponseVo
import com.code.server.game.poker.config.ServerConfig
import com.code.server.game.room.Room
import com.code.server.game.room.service.IRobot
import com.code.server.kafka.MsgProducer
import com.code.server.redis.service.RedisManager
import com.code.server.util.SpringUtil
import com.code.server.util.timer.GameTimer

import scala.collection.JavaConverters._
import scala.util.Random

/**
  * Created by sunxianping on 2019-03-20.
  */
class PaijiuRobot extends IRobot with PaijiuConstant {


  override def execute(): Unit = {}


  override def doExecute(room: Room): Unit = {
    if (room == null || !room.isInstanceOf[RoomPaijiu]) {
      return
    }
    var roomPaijiu = room.asInstanceOf[RoomPaijiu]

    val now = System.currentTimeMillis()


    //机器人可加入 并且 game为空时 机器人加入
    if (room.isInstanceOf[RoomTuitongziGold]) {
      //机器人加入的逻辑
      doAddRobot(roomPaijiu, 1)
      tuitongziRobot(roomPaijiu, now)
      return
    }

    //机器人加入的逻辑
    doAddRobot(roomPaijiu, 0)

    //开始游戏
    doStartGame(roomPaijiu, now)

    if (roomPaijiu.getGame == null) return


    val gamePaijiu = room.getGame.asInstanceOf[GamePaijiu]

    //四人牌九抢庄
    doFightForBanker(roomPaijiu, gamePaijiu, now)
    //机器人下注
    doRobotBet(roomPaijiu, gamePaijiu, now)


    //四人牌九强制下注
    doForceBet(roomPaijiu, gamePaijiu, now)
    //规定时间下注

    //百人牌九 15秒自动发牌
    doAutoDealCard(roomPaijiu, gamePaijiu, now)

    //开牌
    doAutoOpen(roomPaijiu, gamePaijiu, now)


    doAuto100_4_banker(roomPaijiu, gamePaijiu, now)

    doAuto100_4_other(roomPaijiu, gamePaijiu, now)


    //自动切锅 或者继续
    doBreakBanker(roomPaijiu, gamePaijiu, now)


  }

  def tuitongziRobot(room: RoomPaijiu, now: Long): Unit = {
    val rp = room
    if (room.getGame == null) {


      //更新banker
      rp.updateBanker()
      //选定庄家后10秒开局  庄家没变化的话 不许等待10秒
      if ((now - rp.getLastOperateTime) > 500 && rp.getBankerId != 0) {

        println("托管: 开始游戏 100 " + room.getRoomId)
        sendStartGame(rp)
      }

    } else {


      //发牌
      val game = room.getGame.asInstanceOf[GamePaijiu]

      doTuitongziRobotBet(room, game, now)

      if (game.state == STATE_BET || game.state == START_CRAP) {
        if (now - game.lastOperateTime > TUITONGZI_STATE_TIME(STATE_BET)) {
          sendCrapStart(game.bankerId, room.getRoomId)
        }
      }

      if (game.state == STATE_OPEN) {

        if (now - game.lastOperateTime > 1000 * 3) {

          println("托管 开牌")
          for (playerInfo <- game.playerCardInfos.values) {


            if (playerInfo.userId == game.bankerId && playerInfo.group1 == null) {
              sendOpenMsg(playerInfo.userId, room.getRoomId, "1", "")
            } else {
              if (playerInfo.bet != null && playerInfo.group1 == null) {
                sendOpenMsg(playerInfo.userId, room.getRoomId, "1", "")
              }

            }
          }
        }
      }

      //切锅
      if (game.state == STATE_BANKER_BREAK) {
        //10秒自动 继续
        if (now - game.lastOperateTime > TUITONGZI_STATE_TIME(STATE_BANKER_BREAK)) {
          println("托管: 切庄")
          sendBreakBanker(game.bankerId, room.getRoomId, false)
        }
      }


    }
  }

  /**
    * 抢庄
    *
    * @param roomPaijiu
    * @param gamePaijiu
    * @param now
    */
  def doFightForBanker(roomPaijiu: RoomPaijiu, gamePaijiu: GamePaijiu, now: Long): Unit = {
    if (gamePaijiu == null) return
    if (gamePaijiu.isInstanceOf[GamePaijiu100]) return
    val game: GamePaijiuCrazy = gamePaijiu.asInstanceOf[GamePaijiuCrazy]
    if (game.state == STATE_FIGHT_FOR_BANKER) {
      if (now - gamePaijiu.lastOperateTime > STATE_TIME(STATE_FIGHT_FOR_BANKER)) {
        for (playerInfo <- game.playerCardInfos.values) {
          if (!playerInfo.isHasFightForBanker) {
            sendFightBanker(playerInfo.userId, roomPaijiu.getRoomId, true)
          }
        }
      }
    }
  }

  /**
    * 添加机器人
    *
    * @param roomPaijiu
    */
  def doAddRobot(roomPaijiu: RoomPaijiu, minGold: Double): Unit = {
    //4.四人牌九  建房后  机器人每隔一段时间进入  有时间间隔  例如第一个人进入后  第二个人5秒左右进入  第三个人在过5秒  以此类推
    val rand = Random.nextInt(5)
    if (rand < 4) return
    if (roomPaijiu.robotType != 0 && roomPaijiu.getGame == null) {
      val robotNum = roomPaijiu.robotList.size
      //小于所需机器人数量
      if (robotNum < roomPaijiu.robotNum) {
        val needRobotNum = roomPaijiu.robotNum - robotNum
        val needPeopleNum = roomPaijiu.getPersonNumber - roomPaijiu.users.size()
        val needNum = if (needRobotNum < needPeopleNum) needRobotNum else needPeopleNum
        if (needNum != 0) {

          //          val newRobotList = getRobotList(needNum,minGold)
          val newRobotList = getRobotList(1, minGold)
          for (robot <- newRobotList) {
            //发送加入房间
            sendJoinRoom(robot, roomPaijiu.getRoomId)
          }
        }
      }
    }
  }

  /**
    * 开始游戏
    *
    * @param room
    * @param now
    */
  def doStartGame(room: RoomPaijiu, now: Long): Unit = {
    if (room.getGame == null) {
      if (room.isInstanceOf[RoomPaijiuCrazy]) {


        if (room.isInstanceOf[RoomPaijiu100]) {
          val rp = room.asInstanceOf[RoomPaijiu100]
          //更新banker
          rp.updateBanker()
          //选定庄家后10秒开局  庄家没变化的话 不许等待10秒
          if ((now - rp.getLastOperateTime) > 2000 && rp.getBankerId != 0) {

            println("托管: 开始游戏 100 " + room.getRoomId)
            sendStartGame(rp)
          }
        } else {
          val rpc = room.asInstanceOf[RoomPaijiuCrazy]
          //四人不需要等待
          if (rpc.getCurGameNumber > 1) {
            println("托管: 开始游戏 四人 " + room.getRoomId)
            if (rpc.getCurGameNumber == 2) {
              if ((now - rpc.getLastOperateTime) > STATE_TIME(STATE_START)) {
                sendStartGame(rpc)
              }
            } else {
              sendStartGame(rpc)
            }
          }
        }
      }
    }
  }

  /**
    * 切庄
    *
    * @param room
    * @param game
    * @param now
    */
  def doBreakBanker(room: RoomPaijiu, game: GamePaijiu, now: Long): Unit = {
    if (game.state == STATE_BANKER_BREAK) {
      //10秒自动 继续
      if (now - game.lastOperateTime > STATE_TIME(STATE_BANKER_BREAK)) {
        println("托管: 切庄")
        //机器人并且
        if (!room.isInstanceOf[RoomPaijiu100] && isRobot(room, room.getBankerId) && room.bankerScore > 220) {
          sendBreakBanker(game.bankerId, room.getRoomId, true)
        } else {
          sendBreakBanker(game.bankerId, room.getRoomId, false)
        }
      }
    }
  }

  /**
    * 机器人下注
    *
    * @param room
    * @param game
    */
  def doRobotBet(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (!game.isInstanceOf[GamePaijiu100]) return
    if (game.state != STATE_BET) return
    val gamePaijiu = game.asInstanceOf[GamePaijiu100]

    //下注阶段5秒后机器人下注
    if (!gamePaijiu.isRobotBet && time - gamePaijiu.lastOperateTime > 1000 * 3) {
      println("托管: 下注")
      //没下注的机器人开始下注
      var count = 0
      val winIndex = gamePaijiu.getMaxScoreCardIndex()
      gamePaijiu.isRobotBet = true
      val loseIndex = List(1, 2, 3).filter(_ != winIndex)
      //下注选择
      val betNum = List(1, 5, 10, 50)

      for (userId <- room.robotList) {
        val playerInfo = gamePaijiu.playerCardInfos(userId)
        //没下注
        if (playerInfo != null && playerInfo.bet == null && gamePaijiu.bankerId != playerInfo.userId) {

          val one = Random.shuffle(betNum).head
          //只下一道
          val two = 0

          val nextTime = Random.nextInt(1000 * 8)
          //下赢得注
          if (count < room.robotWinner) {
            GameTimer.addTimerNode(nextTime, false, () => sendBet(playerInfo.userId, room.getRoomId, one, two, 0, winIndex))
            //            sendBet(playerInfo.userId, room.getRoomId, one, two, 0, winIndex)
            count += 1
          } else {
            //下输的注
            val index = Random.shuffle(loseIndex).head
            //            sendBet(playerInfo.userId, room.getRoomId, one, two, 0, index)
            GameTimer.addTimerNode(nextTime, false, () => sendBet(playerInfo.userId, room.getRoomId, one, two, 0, index))
          }
        }
      }
    }
  }


  def doTuitongziRobotBet(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (game.state != STATE_BET) return
    val gamePaijiu = game.asInstanceOf[GameTuitongziGold]
    //    if(room.bankerInitScore<=3000) return
    //下注阶段5秒后机器人下注
    if (!gamePaijiu.isRobotBet && time - gamePaijiu.lastOperateTime > 1000 * 8) {
      println("托管: 下注")
      //没下注的机器人开始下注
      gamePaijiu.isRobotBet = true
      //下注选择
      for (userId <- room.robotList) {

        val playerInfo = gamePaijiu.playerCardInfos(userId)
        //没下注
        if (playerInfo != null && playerInfo.bet == null && gamePaijiu.bankerId != playerInfo.userId) {
          var bool = Random.nextBoolean()
          bool = true
          if (bool) {
            val r = List(1, 2, 3)
            val bw = Random.shuffle(r).head
            val betNum = 200
            if (bw == 1) {
              sendBet(playerInfo.userId, room.getRoomId, betNum, 0, 0, 1)
            } else if (bw == 2) {
              sendBet(playerInfo.userId, room.getRoomId, 0, betNum, 0, 2)
            } else {
              sendBet(playerInfo.userId, room.getRoomId, 0, 0, betNum, 3)
            }
          }

        }
      }
    }
  }


  /**
    * 锅1
    *
    * @param num
    * @return
    */
  def getGuoOne(num: Int): (Int, Int, Int) = {
    return (num, 0, 0)
  }

  /**
    * 锅俩
    *
    * @param num
    * @return
    */
  def getGuoTwo(num: Int): (Int, Int, Int) = {
    val one = num / 2
    return (one, num - one, 0)
  }

  /**
    * 锅仨
    *
    * @param num
    * @return
    */
  def getGuoThree(num: Int): (Int, Int, Int) = {
    val one = num / 3
    val two = one
    return (one, two, num - one - two)
  }

  /**
    * 强制下注
    *
    * @param room
    * @param game
    * @param time
    */
  def doForceBet(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (game.isInstanceOf[GamePaijiu100]) return
    if (game.state != STATE_BET) return

    //五秒后让机器人下注
    if (time - game.lastOperateTime < 5000) return
    val isCrazy = "412".equals(room.getGameType)
    for (playerInfo <- game.playerCardInfos.values) {


      //机器人下注
      if (isRobot(room, playerInfo.userId) && playerInfo.bet == null && playerInfo.userId != game.bankerId) {
        //下注数量
        var selfMoney = RedisManager.getUserRedisService.getUserMoney(playerInfo.userId)
        var betNum = if (selfMoney > room.bankerScore) room.bankerScore.toInt else selfMoney.toInt
        val delay = Random.nextInt(3000)
        val isGuoTwo = Random.nextBoolean()

        if (isCrazy) {
          if (isGuoTwo) {
            GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoTwo(betNum)._1, getGuoTwo(betNum)._2, getGuoTwo(betNum)._3, 0))
          } else {
            GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoThree(betNum)._1, getGuoThree(betNum)._2, getGuoThree(betNum)._3, 0))
          }
        } else {

          if (room.bankerScore < 30) {
            if (isGuoTwo) {
              GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoTwo(betNum)._1, getGuoTwo(betNum)._2, getGuoTwo(betNum)._3, 0))
            } else {
              GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoOne(betNum)._1, getGuoOne(betNum)._2, getGuoOne(betNum)._3, 0))
            }
          } else if (room.bankerScore >= 30 && room.bankerScore < 100) {
            if (isGuoTwo) {
              GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoTwo(betNum)._1, getGuoTwo(betNum)._2, getGuoTwo(betNum)._3, 0))
            } else {
              GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoThree(betNum)._1, getGuoThree(betNum)._2, getGuoThree(betNum)._3, 0))
            }
          } else if (room.bankerScore >= 100 && room.bankerScore < 150) {
            GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, getGuoThree(betNum)._1, getGuoThree(betNum)._2, getGuoThree(betNum)._3, 0))
          } else {
            //超过150
            //四人下50
            GameTimer.addTimerNode(delay, false, () => sendBet(playerInfo.userId, room.getRoomId, 50, 50, 50, 0))
          }
        }
      }
    }


    if (time - game.lastOperateTime < STATE_TIME(STATE_BET)) return


    for (playerInfo <- game.playerCardInfos.values) {


      if (playerInfo.bet == null && playerInfo.userId != game.bankerId) {
        //下注数量
        var selfMoney = RedisManager.getUserRedisService.getUserMoney(playerInfo.userId)
        var betNum = if (selfMoney > room.bankerScore) room.bankerScore.toInt else selfMoney.toInt
        val guo = getGuoThree(betNum)
        if (isCrazy) {
          //疯狂下最大
          sendBet(playerInfo.userId, room.getRoomId, guo._1, guo._2, guo._3, 0)
          //          sendBet(playerInfo.userId, room.getRoomId, betNum, 0, 0, 0)

        } else {
          //四人默认下10
          sendBet(playerInfo.userId, room.getRoomId, guo._1, guo._2, guo._3, 0)
          //          sendBet(playerInfo.userId, room.getRoomId, 10, 0, 0, 0)
        }

      }
    }
  }

  def doAutoDealCard(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (!game.isInstanceOf[GamePaijiu100]) return
    if (game.state != STATE_BET && game.state != START_CRAP) return
    if (time - game.lastOperateTime < STATE_TIME(STATE_BET)) return
    println("托管: 自动发牌")
    //强制下注状态结束


    sendCrapStart(game.bankerId, room.getRoomId)

  }


  /**
    * 自动开牌
    *
    * @param room
    * @param game
    * @param time
    */
  def doAutoOpen(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (game.isInstanceOf[GamePaijiu100] && !Room.isHasMode(MODE_2CARD, room.otherMode)) return
    //    if(!game.isInstanceOf[GamePaijiu100]) return
    if (game.state != STATE_OPEN) return

    if (Room.isHasMode(MODE_2CARD, room.otherMode)) {
      //两张牌的牌九自动开 给个5秒做动画时间
      if (time - game.lastOperateTime <= 1000 * 5) return
    } else {
      if (time - game.lastOperateTime <= 1000 * 5) return
      //机器人随机秒数 下注    1.百人牌九  闲家机器人下注后  开牌时   5-7秒自动点击开牌  现在是15秒倒计时开牌
      for (playerInfo <- game.playerCardInfos.values) {
        if (isRobot(room, playerInfo.userId) && playerInfo.cards.nonEmpty && playerInfo.group1 == null) {
          val finalGroup = game.getMaxOpenGroup(playerInfo.cards)
          val nextTime = Random.nextInt(3000)
          GameTimer.addTimerNode(nextTime, false, () => sendOpenMsg(playerInfo.userId, room.getRoomId, finalGroup._1, finalGroup._2))

        }
      }

      //15秒自动开牌
      if (time - game.lastOperateTime <= STATE_TIME(STATE_OPEN)) return
    }

    //    println("托管: 自动开牌")

    for (playerInfo <- game.playerCardInfos.values) {
      if (playerInfo.cards.nonEmpty && playerInfo.group1 == null) {
        val finalGroup = game.getMaxOpenGroup(playerInfo.cards)
        sendOpenMsg(playerInfo.userId, room.getRoomId, finalGroup._1, finalGroup._2)
      }
    }

  }


  def isRobot(room: RoomPaijiu, userId: Long): Boolean = {
    return room.robotList.contains(userId)
  }

  /**
    * 庄家开牌
    *
    * @param room
    * @param game
    * @param time
    */
  def doAuto100_4_banker(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (game.state != STATE_OPEN) return
    if (!game.isInstanceOf[GamePaijiu100]) return
    if (Room.isHasMode(MODE_2CARD, room.otherMode)) return
    if (time - game.lastOperateTime <= 19000) return
    val playerInfo: PlayerCardInfoPaijiu = game.playerCardInfos(room.getBankerId)
    if (playerInfo.cards.nonEmpty && playerInfo.group1 == null) {
      val finalGroup = game.getMaxOpenGroup(playerInfo.cards)
      sendOpenMsg(playerInfo.userId, room.getRoomId, finalGroup._1, finalGroup._2)
    }

  }


  def doAuto100_4_other(room: RoomPaijiu, game: GamePaijiu, time: Long): Unit = {
    if (game.state != STATE_BANKER_OTHER_OPEN) return
    if (!game.isInstanceOf[GamePaijiu100]) return
    if (Room.isHasMode(MODE_2CARD, room.otherMode)) return

    if (time - game.lastOperateTime <= 5000) return

    // 1.百人牌九  闲家机器人下注后  开牌时   5-7秒自动点击开牌  现在是15秒倒计时开牌
    for (playerInfo <- game.playerCardInfos.values) {
      if (isRobot(room, playerInfo.userId) && playerInfo.cards.nonEmpty && playerInfo.group1 == null && playerInfo.userId != room.getBankerId) {
        val finalGroup = game.getMaxOpenGroup(playerInfo.cards)
        val nextTime = Random.nextInt(2000)
        GameTimer.addTimerNode(nextTime, false, () => sendOpenMsg(playerInfo.userId, room.getRoomId, finalGroup._1, finalGroup._2))

      }
    }

    if (time - game.lastOperateTime <= 15000) return
    for (playerInfo <- game.playerCardInfos.values) {
      if (playerInfo.cards.nonEmpty && playerInfo.group1 == null && playerInfo.userId != room.getBankerId) {
        val finalGroup = game.getMaxOpenGroup(playerInfo.cards)
        sendOpenMsg(playerInfo.userId, room.getRoomId, finalGroup._1, finalGroup._2)
      }
    }

  }


  /**
    * 获得所需的机器人
    *
    * @param num
    * @return
    */
  private def getRobotList(num: Int, minGold: Double): List[Long] = {
    val list: java.util.List[String] = RedisManager.getUserRedisService.getRobotPoolUser

    Collections.shuffle(list)
    var count = 0
    import scala.collection.JavaConversions._
    var robotList: List[Long] = List()

    for (userId <- list) {
      val uid: Long = Long.parseLong(userId)
      val roomId = RedisManager.getUserRedisService.getRoomId(uid)
      if (roomId == null) {
        if (minGold == 0 || (minGold > 0 && RedisManager.getUserRedisService.getUserGold(uid) > minGold)) {
          count += 1
          robotList = robotList.+:(uid)
        }
        if (count >= num) {
          return robotList
        }
      }
    }
    robotList
  }


  /**
    * 下注
    *
    * @param userId
    * @param roomId
    * @param one
    * @param two
    * @param three
    * @param index
    */
  private def sendBet(userId: Long, roomId: String, one: Int, two: Int, three: Int, index: Int): Unit = {
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("one" -> one, "two" -> two, "three" -> three, "index" -> index)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "bet", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }

  /**
    * 发送加入房间
    *
    * @param userId
    * @param roomId
    */
  private def sendJoinRoom(userId: Long, roomId: String): Unit = {

    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val put = Map("userId" -> userId)
    val result: ResponseVo = new ResponseVo("roomService", "joinRoom", put.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("roomService", partition, msgKey, result)
  }


  /**
    * 发送开牌消息
    *
    * @param userId
    * @param roomId
    * @param group1
    * @param group2
    */
  private def sendOpenMsg(userId: Long, roomId: String, group1: String, group2: String): Unit = {
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("group1" -> group1, "group2" -> group2)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "open", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }


  /**
    * 发送开始游戏消息
    *
    * @param room
    */
  def sendStartGame(room: Room): Unit = {
    val roomId: String = room.getRoomId
    val partition: Int = SpringUtil.getBean(classOf[ServerConfig]).getServerId
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(0)
    val result: ResponseVo = new ResponseVo("roomService", "startAuto", Map().asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("roomService", partition, msgKey, result)
  }


  /**
    * 发送切庄
    *
    * @param userId
    * @param roomId
    */
  def sendBreakBanker(userId: Long, roomId: String, flag: Boolean): Unit = {
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("flag" -> flag)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "bankerBreak", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }


  /**
    * 发送open
    *
    * @param userId
    * @param roomId
    */
  def sendCrapStart(userId: Long, roomId: String): Unit = {
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("userId" -> userId)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "autoCrap", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }

  /**
    * 发送抢庄
    *
    * @param userId
    * @param roomId
    */
  def sendFightBanker(userId: Long, roomId: String, flag:Boolean): Unit = {
    val partition: Int = RedisManager.getRoomRedisService.getServerId(roomId).toInt
    val msgKey: KafkaMsgKey = new KafkaMsgKey
    msgKey.setRoomId(roomId)
    msgKey.setPartition(partition)
    msgKey.setUserId(userId)
    val param = Map("flag" -> false)
    val result: ResponseVo = new ResponseVo("gamePaijiuService", "fightForBanker", param.asJava)
    SpringUtil.getBean(classOf[MsgProducer]).send2Partition("gamePaijiuService", partition, msgKey, result)
  }
}
