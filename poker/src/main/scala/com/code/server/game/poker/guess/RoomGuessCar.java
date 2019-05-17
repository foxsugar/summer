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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public int chip;

    public static int createRoom(long userId ,int chip,String gameType, String roomType)  {
        //身上的钱够不够
        if(RedisManager.getUserRedisService().getUserGold(userId) < chip){
            return ErrorCode.NOT_HAVE_MORE_MONEY;
        }

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

        RoomGuessCar roomGuessCar = new RoomGuessCar();
        roomGuessCar.personNumber = PERSONNUM;

        roomGuessCar.roomId = getRoomIdStr(genRoomId(serverConfig.getServerId()));
        roomGuessCar.createUser = userId;
        roomGuessCar.gameType = gameType;
        roomGuessCar.roomType = roomType;
        roomGuessCar.bankerId = userId;


        RoomManager.addRoom(roomGuessCar.roomId, "" + serverConfig.getServerId(), roomGuessCar);

        //扣掉
        RedisManager.getUserRedisService().addUserGold(userId, -chip);
        roomGuessCar.bankerScore = chip;
        roomGuessCar.chip = chip;

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        roomGuessCar.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createGuessRoom", roomGuessCar.toVo(userId)), userId);

        roomGuessCar.joinRoom(userId, true);
        return 0;
    }


    public int dissolution(long userId, boolean agreeOrNot, String methodName, long time){

        int rtn =  super.dissolution(userId, agreeOrNot, methodName, time);
        if (rtn == 0 && userId == this.bankerId) {
           //把钱返给庄家
            RedisManager.getUserRedisService().addUserGold(userId, bankerScore);
        }
        return rtn;
    }

    public int quitRoom(long userId) {
        int rtn =  super.quitRoom(userId);
        if(rtn == 0 &&  userId == this.bankerId){
            //把钱返给庄家
            RedisManager.getUserRedisService().addUserGold(userId, bankerScore);
        }
        return rtn;
    }
    public int joinRoom(long userId, boolean isJoin) {
        //要多于5个钻
        if(userId != this.createUser){
            if(RedisManager.getUserRedisService().getUserGold(userId) < 10){
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
        this.game = gameGuessCar;
        this.record.add(redOrGreen);


        long time = 20000;
        this.betEndTimerNode = new TimerNode(System.currentTimeMillis(), time, false, gameGuessCar::sendResult);
        GameTimer.addTimerNode(betEndTimerNode);

        MsgSender.sendMsg2Player("pokerRoomService", "guessCar", time, this.bankerId);
       return 0;
    }


    public static int getAllRoom(long userId){
        List<Map<String,Object>> rooms = new ArrayList<>();
        RoomManager.getInstance().getRooms().values().forEach(r->{
            Map<String, Object> result = new HashMap<>();
            Room roomGuessCar = (Room) r;
            result.put("roomId", roomGuessCar.getRoomId());
            result.put("roomType", roomGuessCar.getRoomType());
            result.put("persionNum", roomGuessCar.getUsers().size());
            if(r instanceof RoomGuessCar){
                RoomGuessCar rm = (RoomGuessCar) r;
                result.put("chip", rm.getChip());
            }
            rooms.add(result);
        });
        MsgSender.sendMsg2Player("pokerRoomService", "getAllRoom", rooms, userId);
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
            roomVo.setGame(this.game.toVo(userId));
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

    public int getChip() {
        return chip;
    }

    public RoomGuessCar setChip(int chip) {
        this.chip = chip;
        return this;
    }
}
