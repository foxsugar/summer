package com.code.server.game.poker.guess;

import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.IfaceRoomVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.constant.response.RoomGuessCarVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunxianping on 2017/12/8.
 */
public class RoomGuessCar extends Room {

    public static final int PERSONNUM = 7;

    public static final int STATE_GUESS = 0;
    public static final int STATE_BET = 1;
    public List<Integer> record = new ArrayList<>();
    public int state = 0;
    public double bankerScore = 0;

    public static int createRoom(long userId ,int chip,String gameType, String roomType)  {
        //身上的钱够不够
        if(RedisManager.getUserRedisService().getUserMoney(userId) < chip){
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }

        RoomGuessCar roomGuessCar = new RoomGuessCar();
        roomGuessCar.personNumber = PERSONNUM;

        roomGuessCar.roomId = getRoomIdStr(genRoomId());
        roomGuessCar.createUser = userId;
        roomGuessCar.gameType = gameType;
        roomGuessCar.roomType = roomType;

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(roomGuessCar.roomId, "" + serverConfig.getServerId(), roomGuessCar);

        //扣掉
        RedisManager.getUserRedisService().addUserMoney(userId, -chip);
        roomGuessCar.bankerScore = chip;

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        roomGuessCar.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createGuessRoom", roomGuessCar.toVo(userId)), userId);

        return 0;
    }


    public int joinRoom(long userId, boolean isJoin) {
        //要多于5个钻
        if(userId != this.createUser){
            if(RedisManager.getUserRedisService().getUserMoney(userId) < 5){
                return ErrorCode.NOT_HAVE_MORE_MONEY;
            }
        }

        int rtn = super.joinRoom(userId, isJoin);
        if (rtn != 0) {
            return rtn;
        }
        return 0;
    }

    public int guessCar(long userId,int redOrGreen){

        this.state = STATE_BET;

       return 0;
    }

    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomGuessCarVo roomVo = new RoomGuessCarVo();
        BeanUtils.copyProperties(this, roomVo);

        roomVo.setState(this.state);
        return roomVo;
    }
}
