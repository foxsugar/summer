package com.code.server.game.poker.doudizhu;


import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

import java.util.List;

/**
 * Created by sunxianping on 2017/3/13.
 */
public class GameDouDiZhuPlus extends GameDouDiZhuGold {


    public void startGame(List<Long> users, Room room) {

        RoomDouDiZhuPlus roomDouDiZhuPlus = (RoomDouDiZhuPlus)room;

        this.users.forEach(userId -> {
        RedisManager.getUserRedisService().addUserMoney(userId, - roomDouDiZhuPlus.getUsesMoney().get(room.getGoldRoomType()));
        });

        this.room = room;
        init(users, room.getBankerId());
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
        RoomManager.getRobotRoom().add(room);
    }
}
