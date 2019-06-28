package com.code.server.game.mahjong.robot;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.mahjong.config.ServerConfig;
import com.code.server.game.mahjong.logic.GameInfo;
import com.code.server.game.mahjong.logic.PlayerCardsInfoMj;
import com.code.server.game.mahjong.logic.RoomInfo;
import com.code.server.game.room.Room;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2018/4/24.
 */
public class MahjongRobot {

    private static long INTERVAL_TIME = 1000L * 30;
    private static long READY_TIME = 1000L * 20;

    public static void execute(RoomInfo room) {
        long now = System.currentTimeMillis();
        GameInfo gameInfo = (GameInfo) room.getGame();
        if (gameInfo != null) {
            if (now- gameInfo.getLastOperateTime()   > INTERVAL_TIME) {
                if (gameInfo.getWaitingforList().size() > 0) {
                    GameInfo.WaitDetail waitDetail = gameInfo.getWaitingforList().get(0);
                    if (waitDetail.isHu) {
                        hu(room, waitDetail.myUserId);
                    } else {
                        guo(room, waitDetail.myUserId);
                    }
                } else {
                    if (gameInfo.getTurnId() != 0) {
                        playCard(room);
                    }
                }
            }
        } else {//在准备状态

            if(room.getCurGameNumber()>1 && now - room.getLastOperateTime() > READY_TIME){
                Map<Long, Integer> map = new HashMap<>();
                map.putAll(room.getUserStatus());
                map.forEach((uid,status) ->{

                    if ((status != Room.STATUS_READY)) {
//                        boolean isOnline = RedisManager.getUserRedisService().getGateId(uid) != null;
//                        if (isOnline) {
                            getReady(room, uid);
//                        }else{
//                            quitRoom(room,uid);
//                        }
//                        quitRoom(room,uid);
//                        MsgSender.sendMsg2Player("roomService", "quitRoomKick", "quit", uid);
                    }

                });
            }

        }
    }

    public static void getReady(Room room, long userId) {
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


    public static void quitRoom(Room room, long userId) {
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

    public static void guo(RoomInfo roomInfo, long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ResponseVo vo = new ResponseVo("gameLogicService", "next",params);
        sendRobotRequest(roomInfo, userId, vo);

    }

    public static void hu(RoomInfo roomInfo, long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ResponseVo vo = new ResponseVo("gameLogicService", "hu",params);
        sendRobotRequest(roomInfo, userId, vo);

    }

    public static void playCard(RoomInfo roomInfo) {
        Map<String, Object> params = new HashMap<>();
        GameInfo gameInfo = (GameInfo) roomInfo.getGame();
        long userId = gameInfo.getTurnId();
        PlayerCardsInfoMj playerCardsInfoMj = gameInfo.getPlayerCardsInfos().get(userId);
        if(playerCardsInfoMj.isMoreOneCard()){
            List<String> cards = playerCardsInfoMj.getCardsNoChiPengGang(playerCardsInfoMj.getCards());
            String card = cards.get(cards.size() - 1);
            params.put("userId", userId);
            params.put("card", card);
            ResponseVo vo = new ResponseVo("gameLogicService", "playCard", params);

            gameInfo.autoPlay = true;
            sendRobotRequest(roomInfo, userId, vo);
        }

    }

    public static void sendRobotRequest(RoomInfo roomInfo,long userId, ResponseVo request) {

        String roomId = roomInfo.getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();

        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        msgKey.setUserId(userId);

        SpringUtil.getBean(MsgProducer.class).send2Partition(request.getService(),partition, msgKey, request);


    }


}
