package com.code.server.game.mahjong.robot;

import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.room.IfaceRoom;
import com.code.server.game.room.service.RoomManager;

/**
 * Created by sunxianping on 2018/4/24.
 */
public class RobotManager {


    public static void excute(){
        for (IfaceRoom room : RoomManager.getInstance().getRobotRoom()) {
            //game 为空 是否要准备
            RoomInfo roomInfo = (RoomInfo) room;
            MahjongRobot.execute(roomInfo);
        }
    }

}
