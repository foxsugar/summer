package com.code.server.game.poker.paijiu

import com.code.server.constant.game.RoomStatistics
import com.code.server.redis.service.RedisManager

/**
  * Created by sunxianping on 2019-06-17.
  */
class RoomTuitongziGold extends RoomPaijiu{




  override def roomRemoveUser(userId: Long): Unit = {
    this.users.remove(userId)
    this.userStatus.remove(userId)
    this.userScores.remove(userId)
    this.roomStatisticsMap.remove(userId)
    //如果在上庄列表里 删掉
    //如果现在是庄家,把钱退给他

    if (this.bankerId == userId) {
      RedisManager.getUserRedisService.addUserGold(userId, this.bankerScore)
      //      this.lastBankerInitScore = bankerInitScore
      this.bankerId = 0
      this.bankerInitScore = 0
      this.bankerScore = 0
    }
    if (bankerScoreMap.contains(userId)) {
      RedisManager.getUserRedisService.addUserGold(userId, bankerScoreMap(userId))
    }
    this.bankerList = this.bankerList.filter(_ != userId)
    this.bankerScoreMap = this.bankerScoreMap.filterKeys(_ != userId)
    this.robotList = this.robotList.filter(_ != userId)
    removeUserRoomRedis(userId)

    //重置房间密码状态
    if (this.users.size() == 0) {
      this.alreadySet = false
      this.usePass = false
      this.pass = 0
    }
  }

  override protected def roomAddUser(userId: Long): Unit = {
    this.users.add(userId)
    this.userStatus.put(userId, 0)
    this.userScores.put(userId, RedisManager.getUserRedisService.getUserGold(userId))
    this.roomStatisticsMap.put(userId, new RoomStatistics(userId))
    this.canStartUserId = users.get(0)
    //代开房
    if (!isCreaterJoin || isClubRoom) this.bankerId = users.get(0)
    //如果是机器人
    if (RedisManager.getUserRedisService.getUserBean(userId).getVip == 1) {
      this.robotList = this.robotList.+:(userId)
    }
    addUser2RoomRedis(userId)
  }

}
