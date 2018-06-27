package com.code.server.game.mahjong.logic;

import com.code.server.constant.game.RoomStatistics;
import com.code.server.redis.service.RedisManager;

/**
 * Created by sunxianping on 2018/6/23.
 */
public class RoomInfoGoldHeLe extends RoomInfo {


    protected void roomAddUser(long userId) {

        this.users.add(userId);
        this.userStatus.put(userId, 0);
        this.userScores.put(userId, 0D);
        if (isGoldRoom()) {
            this.userScores.put(userId, RedisManager.getUserRedisService().getUserGold(userId));
        }
        this.roomStatisticsMap.put(userId, new RoomStatistics(userId));
        this.canStartUserId = users.get(0);

        addUser2RoomRedis(userId);
    }


    @Override
    public void addUserSocre(long userId, double score) {
        //todo 金币改变
//        if (isGoldRoom()) {
        //钱不够的话 能扣多少是多少
        if (score < 0) {
            double temp = -score;
            double nowGold = RedisManager.getUserRedisService().getUserGold(userId);
            if (nowGold < temp) {
                score = -temp;
            }

        }


        double s = userScores.get(userId);
        userScores.put(userId, s + score);


        RedisManager.getUserRedisService().addUserGold(userId, score);

//        }

    }


    @Override
    protected boolean isCanJoinCheckMoney(long userId) {
        //todo 检验金币

        double gold = RedisManager.getUserRedisService().getUserGold(userId);
        if (gold < getEnterGold()) {
            return false;
        }

        return true;
    }


    @Override
    public void clearReadyStatus(boolean isAddGameNum) {

        super.clearReadyStatus(isAddGameNum);
        //todo 如果 金币不够 退出
        int minGold = getOutGold();
        for (long userId : this.users) {
            double gold = RedisManager.getUserRedisService().getUserGold(userId);
            if (gold < minGold) {
                dissolutionRoom();
                return;
            }
        }
    }
}
