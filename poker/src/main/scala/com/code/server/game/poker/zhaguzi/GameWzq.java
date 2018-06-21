package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.cow.GameWzqVo;
import com.code.server.game.room.Game;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class GameWzq extends Game {


    private Room roomWzq;

    public int admitDefeat(long userId){

        double gold = RedisManager.getUserRedisService().getUserGold(userId);
        if (gold < this.roomWzq.getMultiple()) {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }
        if (userId == this.roomWzq.getBankerId()) {
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }

        List<Long> tempUser = new ArrayList<>();
        tempUser.addAll(this.users);
        tempUser.remove((Long) userId);
        long other = tempUser.get(0);
        double gold1 = RedisManager.getUserRedisService().addUserGold(userId, -this.roomWzq.getMultiple());

        double gold2 = RedisManager.getUserRedisService().addUserGold(other, this.roomWzq.getMultiple());

        Map<Long, Object> golds = new HashMap<>();
        golds.put(userId, gold1);
        golds.put(other, gold2);
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "admitDefeat", golds), this.getUsers());



        sendResult();

        this.roomWzq.clearReadyStatus(true);

        sendFinalResult();

        return 0;
    }



    protected void sendFinalResult() {
        //所有牌局都结束
        if (this.roomWzq.isRoomOver()) {
            MsgSender.sendMsg2Player("gameService", "gameWzqFinalResult", "finalResult", users);
            RoomManager.removeRoom(this.roomWzq.getRoomId());
        }
    }

    protected void sendResult() {
        MsgSender.sendMsg2Player("gameService", "gameWzqResult", "gameResult", users);
    }

    public int move(long userId, int x, int y){
        return 0;
    }


    @Override
    public void startGame(List<Long> users, Room room) {
        this.roomWzq = room;
        this.users.addAll(room.getUsers());
    }

    @Override
    public IfaceGameVo toVo() {
        GameWzqVo gameWzqVo = new GameWzqVo();
        gameWzqVo.getUsers().addAll(this.users);
        return gameWzqVo;
    }

    @Override
    public IfaceGameVo toVo(long watchUser) {
        GameWzqVo gameWzqVo = new GameWzqVo();
        gameWzqVo.getUsers().addAll(this.users);
        return gameWzqVo;
    }
}
