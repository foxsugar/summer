package com.code.server.game.poker.cow;

import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：${project_name}
 * 类名称：${type_name}
 * 类描述：
 * 创建人：Clark
 * 创建时间：${date} ${time}
 * 修改人：Clark
 * 修改时间：${date} ${time}
 * 修改备注：
 *
 * @version 1.0
 */
public class CowRobot implements ICowRobot,IGameConstant {

    @Override
    public void execute() {
        //TODO
        RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null && room.getGame()==null) {
            return;
        }
        if(room != null && room.getGame() == null){
            if(room instanceof RoomCow){
                RoomCow roomCow = (RoomCow)room;
                long now = System.currentTimeMillis();
                if(now > roomCow.getRoomLastTime() + SECOND * 10){
                    getReady(roomCow);
                }
            }
        }
        if (room.getGame() instanceof GameCow) {
            GameCow game = (GameCow) room.getGame();
            long now = System.currentTimeMillis();
            //执行
            if(now > game.lastOperateTime + SECOND * 10){
                switch (game.step) {
                    case STEP_RAISE:
                        raise(game);
                        break;
                    case STEP_COMPARE:
                        compare(game);
                        break;
                }
            }
        }
    }

    @Override
    public void raise(GameCow game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();
        put.put("addChip",1);

        for (PlayerCow p : game.getPlayerCardInfos().values()) {
            if(p.userId!=game.room.getBankerId() && 0==p.getRaise()){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "raise",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void compare(GameCow game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();

        for (PlayerCow p : game.getPlayerCardInfos().values()) {
            if(0==p.getKill()){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "compare",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }


    @Override
    public void getReady(RoomCow roomCow) {

        String roomId = roomCow.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        for (Long l:roomCow.getUserStatus().keySet()) {
            if(0==roomCow.getUserStatus().get(l)){
                msgKey.setUserId(l);
            }
        }

        Map<String, Object> put = new HashMap();


        ResponseRobotVo result = new ResponseRobotVo("roomService", "getReady",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("roomService",partition, msgKey, result);

    }
}
