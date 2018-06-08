package com.code.server.game.poker.zhaguzi;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.robot.IRobot;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.poker.tuitongzi.GameTuiTongZi;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.config.IConstant;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/6/8.
 */
public class YSZRobotImpl implements YSZRobot{

    public static final long SECOND =  1000L;//秒;

    @Override
    //自动过
    public void pass(GameYSZ game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        Map<String, Object> put = new HashMap<>();
        put.put("userId", game.curUserId);
        ResponseRobotVo result = new ResponseRobotVo("gameService", "fold",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void execute() {
        RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null && room.getGame()==null) {
            return;
        }

        if (room.getGame() != null && room.getGame() instanceof GameTuiTongZi) {
            GameYSZ game =  (GameYSZ) room.getGame();
            long now = System.currentTimeMillis();
            //执行
            if(now > game.lastOperateTime + SECOND * 30){
                pass(game);
            }
        }
    }
}
