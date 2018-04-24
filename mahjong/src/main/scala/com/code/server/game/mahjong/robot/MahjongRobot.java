package com.code.server.game.mahjong.robot;

import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.RoomInfo;

/**
 * Created by sunxianping on 2018/4/24.
 */
public class MahjongRobot {

    private static long INTERVAL_TIME = 30000L;
    public static void execute(RoomInfo roomInfo) {
        long now = System.currentTimeMillis();
        GameInfo gameInfo = (GameInfo) roomInfo.getGame();
        if (gameInfo != null) {
            if (gameInfo.getLastOperateTime() - now > INTERVAL_TIME) {
                if (gameInfo.getWaitingforList().size() > 0) {

                }
            }
        }
    }





}
