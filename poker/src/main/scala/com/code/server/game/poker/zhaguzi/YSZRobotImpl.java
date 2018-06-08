package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.robot.IRobot;
import com.code.server.game.poker.tuitongzi.GameTuiTongZi;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.redis.config.IConstant;

/**
 * Created by dajuejinxian on 2018/6/8.
 */
public class YSZRobotImpl implements YSZRobot{
    @Override
    public void pass(GameYSZ game) {

    }

    @Override
    public void execute() {
        RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null && room.getGame()==null) {
            return;
        }
//        if (room.getGame() != null && room.getGame() instanceof GameYSZ) {
//            GameTuiTongZi game = (GameTuiTongZi) room.getGame();
//            long now = System.currentTimeMillis();
//            //执行
//            if(now > game.lastOperateTime + IConstant.SECOND_5 * 15){
//                switch (game.stag) {
//
//                }
//            }
//        }
    }
}
