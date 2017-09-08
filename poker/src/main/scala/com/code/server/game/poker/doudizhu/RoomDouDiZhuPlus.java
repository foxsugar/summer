package com.code.server.game.poker.doudizhu;


import com.code.server.game.room.Game;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.timer.GameTimer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Clark on 2017/8/7.
 */
public class RoomDouDiZhuPlus extends RoomDouDiZhu {

    public static final int PERSONNUM = 3;

    private static final Map<Double,Integer> needsMoney = new HashMap<>();
    private static final Map<Double,Integer> usesMoney = new HashMap<>();

    static {
        needsMoney.put(10D,60);
        needsMoney.put(50D,300);
        needsMoney.put(100D,1000);

        usesMoney.put(10D,2);
        usesMoney.put(50D,4);
        usesMoney.put(100D,6);
    }

    @Override
    protected Game getGameInstance() {
        return new GameDouDiZhuPlus();
    }


    public void startGame() {
        this.isInGame = true;
        Game game = getGameInstance();
        this.game = game;
        //游戏开始 代建房 去除定时解散
        if(!isOpen && !this.isCreaterJoin()){
            GameTimer.removeNode(prepareRoomTimerNode);
        }
        game.startGame(users, this);
        this.isOpen = true;
        pushScoreChange();
    }


    public void spendMoney() {
        this.users.forEach(userId -> {
            RedisManager.getUserRedisService().addUserMoney(userId, - usesMoney.get(goldRoomType));
            if (isAddGold()) RedisManager.addGold(userId, usesMoney.get(goldRoomType) / 10);
        });
    }


    protected boolean isCanJoinCheckMoney(long userId) {
        if (RedisManager.getUserRedisService().getUserMoney(userId) < needsMoney.get(goldRoomType)) {
            return false;
        }
        return true;
    }


    public static Map<Double, Integer> getNeedsMoney() {
        return needsMoney;
    }

    public static Map<Double, Integer> getUsesMoney() {
        return usesMoney;
    }


}