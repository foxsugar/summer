package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.IdWorker;
import com.code.server.util.SpringUtil;

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

    public static int createRoom(long userId, String roomType,String gameType, int gameNumber, int personNumber, boolean isJoin, int multiple){
        RoomTuiTongZi room = getRoomInstance(roomType);

        room.personNumber = personNumber;
        room.roomId = getRoomIdStr(genRoomId());
        room.createUser = userId;
        room.gameType = gameType;
        room.isCreaterJoin = isJoin;
        room.multiple = multiple;
        room.bankerId = userId;

        ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);
        RoomManager.addRoom(room.roomId, "" + serverConfig.getServerId(), room);
        IdWorker idWorker = new IdWorker(serverConfig.getServerId(), 0);
        room.setUuid(idWorker.nextId());

        MsgSender.sendMsg2Player(new ResponseVo("gameTuiTongZiService", "createTuiTongZiRoom", room.toVo(userId)), userId);

        return 0;
    }
}
