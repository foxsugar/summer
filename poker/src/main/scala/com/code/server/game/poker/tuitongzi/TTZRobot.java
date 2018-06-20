package com.code.server.game.poker.tuitongzi;

import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.poker.zhaguzi.GameBaseYSZ;
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
public class TTZRobot implements IGameConstant,ITTZRobot {

    @Override
    public void execute() {
        RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null && room.getGame()==null) {
            return;
        }
//        if(room != null && room.getGame() == null){
//            if(room instanceof RoomTuiTongZi){
//                RoomTuiTongZi roomTTZ = (RoomTuiTongZi)room;
//                long now = System.currentTimeMillis();
//                if(now > roomTTZ.getRoomLastTime() + SECOND * 15){
//                    getReady(roomTTZ);
//                }
//            }
//        }
        if (room.getGame() != null && room.getGame() instanceof GameTuiTongZi) {
            GameTuiTongZi game = (GameTuiTongZi) room.getGame();
            long now = System.currentTimeMillis();
            //执行
            if(now > game.lastOperateTime + SECOND * 15){
                switch (game.state) {
                    case 4:
                        crap(game);
                        break;
//                    case 3:
////                        bet(game);
//                        break;
                    case 5:
                        open(game);
                        break;
                }
            }
        }
    }

    @Override
    public void crap(GameTuiTongZi game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();

        for (PlayerTuiTongZi p : game.getPlayerCardInfos().values()) {
            if(p.getUserId()==game.room.getBankerId()){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "crap",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void bet(GameTuiTongZi game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();

        if (game instanceof GameTuiZiKXHY_BWZ){
            put.put("zhu",1 + 1000);
        }else {
            put.put("zhu",1);
        }

        for (PlayerTuiTongZi p : game.getPlayerCardInfos().values()) {
            if(p.getUserId()!=game.room.getBankerId() && p.getBet()==null){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "bet",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }


    @Override
    public void open(GameTuiTongZi game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);

        Map<String, Object> put = new HashMap();

        for (PlayerTuiTongZi p : game.getPlayerCardInfos().values()) {
            if(!p.isOpen()){
                msgKey.setUserId(p.getUserId());
                put.put("userId",p.getUserId());
            }
        }

        ResponseRobotVo result = new ResponseRobotVo("gameService", "open",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void getReady(RoomTuiTongZi room) {
        String roomId = room.getRoomId();



        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        for (Long l:room.getUserStatus().keySet()) {
            if(0==room.getUserStatus().get(l)){
                msgKey.setUserId(l);
            }
        }

        Map<String, Object> put = new HashMap();

        ResponseRobotVo result = new ResponseRobotVo("roomService", "getReady",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("roomService",partition, msgKey, result);
    }
}
