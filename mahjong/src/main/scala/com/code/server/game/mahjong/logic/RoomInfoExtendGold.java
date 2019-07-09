package com.code.server.game.mahjong.logic;

import com.code.server.game.mahjong.config.ServerConfig;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.Room;
import com.code.server.game.room.RoomExtendGold;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.SpringUtil;

/**
 * Created by sunxianping on 2018/4/12.
 */
public class RoomInfoExtendGold extends RoomExtendGold {

    public Room getDefaultGoldRoomInstance(long userId, String roomType, String gameType, Integer goldRoomType) {


//        Room room = createRoom(userId, roomType, gameType, goldRoomType);
        return create(userId, roomType, gameType,goldRoomType);

//        return room;
    }
//    {"service":"mahjongRoomService","method":"createRoomByUser",
//            "params":{"userId":"10001154","modeTotal":"106","mode":118784,"multiple":"1","gameNumber":"8","personNumber":"4","gameType":"ZHANGLEBAO","roomType":"1"}})

    public static Room create(long userId, String roomType, String gameType,int goldRoomType){
        switch (gameType){
            case "ZHANGLEBAO":
                return createKd(userId, roomType, gameType,goldRoomType);
            default:
                return null;

        }

    }

    public static Room createKd(long userId, String roomType, String gameType,int goldRoomType){
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        String roomId = Room.getRoomIdStr(Room.genRoomId(serverId));
        RoomInfo room = new RoomInfoZLB();
        room.setRoomId(roomId);
        room.setGameType(gameType);
        room.setRoomType(roomType);
        room.setGoldRoomType(goldRoomType);
        room.setGoldRoomPermission(IfaceRoom.GOLD_ROOM_PERMISSION_DEFAULT);
        room.setMultiple(1);
        room.setMode("118784");
        room.setModeTotal("106");
        room.isRobotRoom = true;
        room.setPersonNumber(4);
        room.setGameNumber(4);
        room.setAA(true);
        room.setEach("0");
        room.setCreaterJoin(false);
        room.init(roomId, userId, room.getModeTotal(), room.getMode(), room.getMultiple(), room.getGameNumber(), room.getPersonNumber(), userId, 0, room.getMustZimo());
//        room.setYipaoduoxiang(true);
//        room.setCanChi(false);
//        room.setHaveTing(false);
        return room;
    }


    public void add2GoldPool() {
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        RoomManager.getInstance().addNotFullGoldRoom(this);
        RoomManager.addRoom(this.getRoomId(), "" + serverId, this);
    }



    public static Room createRoom(long userId, String roomType, String gameType, Integer goldRoomType) {
        int serverId = SpringUtil.getBean(ServerConfig.class).getServerId();
        String roomId = Room.getRoomIdStr(Room.genRoomId(serverId));
        RoomInfo room = new RoomInfo();
        room.setRoomId(roomId);
        room.setGameType(gameType);
        room.setRoomType(roomType);
        room.setGoldRoomType(goldRoomType);
        room.setGoldRoomPermission(IfaceRoom.GOLD_ROOM_PERMISSION_DEFAULT);
        room.setMultiple(goldRoomType);


        room.setMode("0");
        room.setModeTotal("50");

        room.setMultiple(goldRoomType);
        room.isRobotRoom = true;


        room.setPersonNumber(4);
        room.setAA(false);
        room.setEach("0");

        room.init(roomId, userId, room.getModeTotal(), room.getMode(), room.getMultiple(), room.getGameNumber(), room.getPersonNumber(), userId, 0, room.getMustZimo());
        room.setCreaterJoin(false);
        room.setYipaoduoxiang(true);
        room.setCanChi(false);
        room.setHaveTing(false);
        return room;
    }
}
//mj_lq_hm_101 = {"service":"mahjongRoomService","method":"createRoomButNotInRoom","params":{"userId":"5","modeTotal":"100","mode":"2","multiple":"1","gameNumber":"1","personNumber":"4","gameType":"HM","mustZimo":false,"haveTing":"true","roomType":"1"}}
