package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.zhaguzi.GameYSZ;
import com.code.server.game.poker.zhaguzi.RoomYSZ;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;

import java.util.List;

/**
 * Created by sunxianping on 2019-06-05.
 */
public class GameYSZLongcheng extends GameYSZ {




    public void startGame(List<Long> users, Room room) {

        com.code.server.game.poker.zhaguzi.PokerItem.isYSZ = true;

        this.room = (RoomYszLongcheng) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }


}
