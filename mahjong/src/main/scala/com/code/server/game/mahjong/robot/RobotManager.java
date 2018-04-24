package com.code.server.game.mahjong.robot;

import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.room.IfaceGame;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.service.RoomManager;

/**
 * Created by sunxianping on 2018/4/24.
 */
public class RobotManager {


    private static long time = 1000 * 30;
    public void excute(){
        for (IfaceRoom room : RoomManager.getInstance().getRobotRoom()) {
            //game 为空 是否要准备
            IfaceGame game = room.getGame();
            if (room.getGame() == null) {

            } else {
                if (game instanceof GameInfo) {
                    GameInfo g = (GameInfo)game;
                    long now = System.currentTimeMillis();
                    if (g.getLastOperateTime() - now > time) {

                    }
                }

            }
        }
    }

}
