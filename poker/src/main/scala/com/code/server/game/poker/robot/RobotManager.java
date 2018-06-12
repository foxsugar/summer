package com.code.server.game.poker.robot;


import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.IRobot;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sunxianping on 2017/5/16.
 */
public class RobotManager implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RobotManager.class);

    private static RobotManager instance;

    private ServerConfig serverConfig = SpringUtil.getBean(ServerConfig.class);

    private RobotManager() {
    }

    public static RobotManager getInstance() {
        if (instance == null) {
            instance = new RobotManager();
        }
        return instance;
    }

    public List<IRobot> robots = new ArrayList<>();


    public RobotManager addRobot(IRobot... robot) {

        this.robots.addAll(Arrays.asList(robot));


        return this;
    }


    @Override
    public void run() {
        while (true) {
            try {
                List<Room> rooms = RoomManager.getInstance().getRobotRoom();
                for (Room room : rooms) {
//                    robots.forEach(IRobot::execute());
                    for (IRobot robot : robots) {
                        robot.doExecute(room);
                    }
                }
//                robots.forEach(IRobot::execute);
                //休眠
                Thread.sleep(serverConfig.getRobotExeCycle());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("机器人执行出现错误: " + e);

            }
        }
    }
}
