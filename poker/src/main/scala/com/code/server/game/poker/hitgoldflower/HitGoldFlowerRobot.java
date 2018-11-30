package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.IRobot;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2018-09-28.
 */
public class HitGoldFlowerRobot implements IRobot {
    @Override
    public void execute() {

    }

    @Override
    public void doExecute(Room room) {
        if (room == null) {
            return;
        }

        if (!(room instanceof RoomHitGoldFlower)) {
            return;
        }

        RoomHitGoldFlower r = (RoomHitGoldFlower) room;
        long now = System.currentTimeMillis();
        if (r.getGame() != null) {
            GameHitGoldFlower game = (GameHitGoldFlower) r.getGame();
            //执行
            if (now > game.lastOperateTime + r.time * 1000) {
//                pass(game);
                pass(game);
            }
        } else {

//            System.out.println("last op " + ((RoomYSZ) room).getLastReadyTime());
            //如果没在游戏中
            if (r.autoReady) {

                if (room.getCurGameNumber() > 1 && now - room.getLastOperateTime() > 1000 * r.time) {
//
                    r.userStatus.forEach((id,status)->{
                        if (status == 0) {
                        getReady(r, id);
                        }
                    });
                }
            }


        }
    }


    public void getReady(Room room, long userId) {
        String roomId = room.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        msgKey.setUserId(userId);

        Map<String, Object> put = new HashMap();


        ResponseVo result = new ResponseVo("roomService", "getReady", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("roomService", partition, msgKey, result);

    }


    public void pass(GameHitGoldFlower game) {

        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        Map<String, Object> put = new HashMap<>();
        put.put("userId", game.curUserId);
        msgKey.setUserId(game.curUserId);
        ResponseRobotVo result = new ResponseRobotVo("gameService", "fold", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService", partition, msgKey, result);
    }
}
