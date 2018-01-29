package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.exception.DataNotFoundException;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;
import com.code.server.util.timer.TimerNode;

public class RoomTuiTongZi extends Room{

    private long bankerScore;

    private long bankerInitScore;

    public long getBankerScore() {
        return bankerScore;
    }

    public void setBankerScore(long bankerScore) {
        this.bankerScore = bankerScore;
    }

    public long getBankerInitScore() {
        return bankerInitScore;
    }

    public void setBankerInitScore(long bankerInitScore) {
        this.bankerInitScore = bankerInitScore;
    }

    public static RoomTuiTongZi getRoomInstance(String roomType){
        switch (roomType) {
            case "5":
                return new RoomTuiTongZi();
            default:
                return new RoomTuiTongZi();
        }
    }

    public static int createRoom(long userId, String roomType,String gameType, int gameNumber, int personNumber, boolean isJoin, int multiple) throws DataNotFoundException {
        RoomTuiTongZi room = getRoomInstance(roomType);

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId());
        room.createUser = userId;
        room.gameType = gameType;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;

        room.init(gameNumber, multiple);

        int code = room.joinRoom(userId, isJoin);
        if (code != 0) {
            return code;
        }


        //代建房 定时解散
        if (!isJoin) {
            //给代建房 开房者 扣钱
            if(RedisManager.getUserRedisService().getUserMoney(userId) < room.createNeedMoney){
                return ErrorCode.CANNOT_CREATE_ROOM_MONEY;
            }
            room.spendMoney();
            TimerNode prepareRoomNode = new TimerNode(System.currentTimeMillis(), IConstant.HOUR_1, false, room::dissolutionRoom);
            room.prepareRoomTimerNode = prepareRoomNode;
            GameTimer.addTimerNode(prepareRoomNode);
        }

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);

        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("pokerRoomService", "createTTZRoom", room.toVo(userId)), userId);

        return 0;
    }

    private void init(){}

}
