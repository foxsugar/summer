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
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;
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
    public transient TimerNode betEndTimerNode;//结算定时器

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
        roomGuessCar.bankerId = userId;

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(roomGuessCar.roomId, "" + serverConfig.getServerId(), roomGuessCar);

        //扣掉
        RedisManager.getUserRedisService().addUserMoney(userId, -chip);
        roomGuessCar.bankerScore = chip;

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        roomGuessCar.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createGuessRoom", roomGuessCar.toVo(userId)), userId);

        roomGuessCar.joinRoom(userId, true);
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

        if(this.state == STATE_BET){
            return ErrorCode.STATE_ERROR;
        }
        if (redOrGreen != 0 && redOrGreen != 1) {
            return ErrorCode.STATE_ERROR;
        }
        this.state = STATE_BET;

        GameGuessCar gameGuessCar = new GameGuessCar();
        gameGuessCar.startGame(users,this,redOrGreen);
        this.record.add(redOrGreen);


        TimerNode betEndTimerNode = new TimerNode(System.currentTimeMillis(), 20000, false, gameGuessCar::sendResult);
        GameTimer.addTimerNode(betEndTimerNode);

        MsgSender.sendMsg2Player("pokerRoomService", "guessCar", 0, this.bankerId);
       return 0;
    }

    @Override
    public IfaceRoomVo toVo(long userId) {
        RoomGuessCarVo roomVo = new RoomGuessCarVo();
        BeanUtils.copyProperties(this, roomVo);
        RedisManager.getUserRedisService().getUserBeans(users).forEach(userBean -> roomVo.userList.add(userBean.toVo()));

        roomVo.setState(this.state);
        roomVo.setRecord(this.record);
        roomVo.setBankerScore(this.bankerScore);
        if (this.game != null) {
            roomVo.setGame(this.game.toVo());
        }
        if (this.state == 1) {
            roomVo.setRemainTime(this.betEndTimerNode.getNextTriggerTime() - System.currentTimeMillis());
        }
        return roomVo;
    }

    public double getBankerScore() {
        return bankerScore;
    }

    public void setBankerScore(double bankerScore) {
        this.bankerScore = bankerScore;
    }
}
