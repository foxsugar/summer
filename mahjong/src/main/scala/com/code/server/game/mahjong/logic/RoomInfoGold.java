package com.code.server.game.mahjong.logic;

/**
 * Created by sunxianping on 2018/4/12.
 */
public class RoomInfoGold extends RoomInfo {

    public void getDefaultGoldRoomInstance(long userId, String roomType, String gameType, Integer goldRoomType ) {

        this.goldRoomPermission = GOLD_ROOM_PERMISSION_DEFAULT;
        this.goldRoomType = goldRoomType;
        this.roomType = roomType;
        this.gameType = gameType;

        this.mode = "0";
        this.modeTotal = "50";

        this.multiple = (int)goldRoomType;




        this.personNumber = 4;
        this.setAA(false);
        this.setEach("0");

        this.init(roomId, userId, modeTotal, mode, multiple, gameNumber, personNumber, userId, 0,mustZimo);
        this.setCreaterJoin(false);
        this.setYipaoduoxiang(true);
        this.setCanChi(false);
        this.setHaveTing(false);

    }
}
//mj_lq_hm_101 = {"service":"mahjongRoomService","method":"createRoomButNotInRoom","params":{"userId":"5","modeTotal":"100","mode":"2","multiple":"1","gameNumber":"1","personNumber":"4","gameType":"HM","mustZimo":false,"haveTing":"true","roomType":"1"}}
