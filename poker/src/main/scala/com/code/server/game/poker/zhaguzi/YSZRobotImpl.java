package com.code.server.game.poker.zhaguzi;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dajuejinxian on 2018/6/8.
 */
public class YSZRobotImpl implements YSZRobot {

    protected static final Logger logger = LoggerFactory.getLogger(YSZRobotImpl.class);
    public static final long SECOND = 1000L;//秒;
    public static final long COUNT = 90;

    @Override
    //自动过
    public void pass(GameYSZ game) {

        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        logger.info("{}  robot pass", game.curRoundNumber);
        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        Map<String, Object> put = new HashMap<>();
        put.put("userId", game.curUserId);
        msgKey.setUserId(game.curUserId);
        ResponseRobotVo result = new ResponseRobotVo("gameService", "fold", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService", partition, msgKey, result);
    }

    public void exe(GameYSZ game) {

        if (game == null || game.curUserId == 0) {
            logger.info("牌局结束");
            return;
        }

        if (game.aliveUser.size() < 2) {
            logger.info("牌局结束");
            return;
        }

//        int round = 0;
//        for (PlayerYSZ playerYSZ : game.playerCardInfos.values()){
//
//            if (round < playerYSZ.getCurRoundNumber()){
//                round = playerYSZ.getCurRoundNumber();
//            }
//        }
//
//        if (round == 1){
//            bet(game);
//        }else {
//            pass(game);
//        }
        pass(game);
    }

    @Override
    public void bet(GameYSZ game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        Map<String, Object> put = new HashMap<>();
        put.put("userId", game.curUserId);
        msgKey.setUserId(game.curUserId);
        ResponseRobotVo result = new ResponseRobotVo("gameService", "call", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService", partition, msgKey, result);

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

    public void quitRoom(Room room, long userId) {
        String roomId = room.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        msgKey.setUserId(userId);

        Map<String, Object> put = new HashMap();


        ResponseVo result = new ResponseVo("roomService", "quitRoom", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("roomService", partition, msgKey, result);

    }

    public void startGame(Room room) {
        String roomId = room.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        msgKey.setUserId(0);

        Map<String, Object> put = new HashMap();


        ResponseVo result = new ResponseVo("roomService", "startAuto", put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("roomService", partition, msgKey, result);

    }

    @Override
    public void execute() {
        RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null) {
            return;
        }


        if (!(room instanceof RoomYSZ)) {
            return;
        }
        RoomYSZ r = (RoomYSZ) room;
        long now = System.currentTimeMillis();
        if (r.getGame() != null) {
            GameYSZ game = (GameYSZ) r.getGame();
            //执行
            if (now > game.lastOperateTime + SECOND * COUNT) {
//                pass(game);
                exe(game);
            }
        } else {

//            System.out.println("last op " + room.getLastOperateTime());
//            System.out.println("now " + now);
            //如果没在游戏中
            if (room.getCurGameNumber() > 1 && now - room.getLastOperateTime() > 1000 * 15) {
//                logger.info("xxxxxxx:now{}:lastOverTime{}==inter:{}", now, ((RoomYSZ) room).getLastReadyTime(), (now - ((RoomYSZ) room).getLastReadyTime())/ 1000.0);
                Map<Long, Integer> map = new HashMap<>();
                map.putAll(room.getUserStatus());
                map.forEach((uid, status) -> {
                    if ((status != Room.STATUS_READY)) {
//                        boolean isOnline = RedisManager.getUserRedisService().getGateId(uid) != null;
//                        if (isOnline) {
//                            getReady(room, uid);
//                        }else{
//                            quitRoom(room,uid);
//                        }
                        quitRoom(room, uid);
                    }
                });
            }
            if (r.getUsers().size() >= 2) {
                long t = now - r.getLastReadyTime();
                if (r.isAllReady() && t > SECOND * 3) {
                    startGame(r);
                }
            }

        }

    }
}
