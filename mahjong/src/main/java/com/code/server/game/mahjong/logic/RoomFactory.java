package com.code.server.game.mahjong.logic;

/**
 * Created by win7 on 2016/12/26.
 */
public class RoomFactory {
    public static final int roomTypeJL = 1;//晋龙
//    大运 DY
//    晋龙 JL
//    胡同 HT
//    易和 YH

    public static RoomInfo getRoomInstance(String gameType) {
        switch (gameType) {
            case "JL"://晋龙
            case "DS"://都市
                return new RoomInfoJL().setGameType(gameType);
            case "HT":
                return new RoomInfo().setGameType(gameType).setHasGangBlackList(false);
            default:
                return new RoomInfo().setGameType(gameType);
        }
    }
}
