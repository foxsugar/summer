package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
import com.code.server.constant.game.IGameConstant;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.config.ServerConfig;
import com.code.server.game.poker.robot.ResponseRobotVo;
import com.code.server.game.room.Room;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clark on 2017/5/16.
 */
public class DouDiZhuGoldRobot implements IDouDiZhuRobot,IGameConstant {

    private static boolean USE_AUTO_TIME = false;
    @Override
    public void execute() {
//        RoomManager.getInstance().getFullGoldRoom().values().forEach(list->list.forEach());
       RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
        //RoomManager.getRobotRoom().forEach(this::doExecute);
    }

    static{
        USE_AUTO_TIME = SpringUtil.getBean(ServerConfig.class).getAutoInterval()!=0;
    }

    /**
     * 获得当前trun的人
     * @return
     */
    public static long getCurUser(GameDouDiZhu game){
        switch (game.step){
            case STEP_JIAO_DIZHU:
                return game.canJiaoUser;
            case STEP_QIANG_DIZHU:
                return game.canQiangUser;
            case STEP_PLAY:
                return game.playTurn;
        }
        return 0;
    }


    public static boolean isFire(GameDouDiZhu gameInfo){
        if(!USE_AUTO_TIME) return false;
        long userId = getCurUser(gameInfo);
        if(gameInfo.autoTimes.getOrDefault(userId, 0)>=3) return true;
        return false;
    }

    public static void addAutoTimes(GameDouDiZhu gameInfo, long userId) {
        gameInfo.autoTimes.put(userId, gameInfo.autoTimes.getOrDefault(userId, 0) + 1);
        gameInfo.autoStatus.put(userId, 1);
    }

    public void doExecute(Room room) {
        if(room == null) return;
        long now = System.currentTimeMillis();
        if ( room.getGame() != null) {
            if (room.getGame() instanceof GameDouDiZhu) {
                GameDouDiZhu game = (GameDouDiZhu) room.getGame();
                //执行
                if(now > game.lastOperateTime + SECOND * 10 || isFire(game)){
                    long curUserId = getCurUser(game);
                    switch (game.step) {
                        case STEP_JIAO_DIZHU:
                            jiaoDizhu(game);
                            break;
                        case STEP_QIANG_DIZHU:
                            qiangDizhu(game);
                            break;
                        case STEP_PLAY:
                            play(game);
                            break;
                    }
                    addAutoTimes(game,curUserId);
                }
            }

        }else{
            if(room.getCurGameNumber()>1 && now - room.getLastOperateTime() > 10000){
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

    @Override
    public void jiaoDizhu(GameDouDiZhu game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();
        msgKey.setUserId(game.canJiaoUser);
        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        //按service的名字 分topic
        //{"service":"gameService","method":"jiaoDizhu","params":{"isJiao":true,"score":3}}

        Map<String, Object> put = new HashMap<>();
        put.put("isJiao",false);
        ResponseRobotVo result = new ResponseRobotVo("gameService", "jiaoDizhu",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);

    }

    @Override
    public void qiangDizhu(GameDouDiZhu game) {

        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();
        msgKey.setUserId(game.canQiangUser);
        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        //按service的名字 分topic
        //{"service":"gameService","method":"qiangDizhu","params":{"isQiang":false}}
        Map<String, Object> put = new HashMap<>();
        put.put("isQiang",false);
        ResponseRobotVo result = new ResponseRobotVo("gameService", "qiangDizhu",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }

    @Override
    public void play(GameDouDiZhu game) {
        PlayerCardInfoDouDiZhu playerInfo = game.getPlayerCardInfos().get(game.playTurn);
        if(playerInfo.cards.size() ==0){
            return;
        }
        if (game.lastCardStruct == null || game.playTurn == game.lastCardStruct.getUserId()) {

            CardStruct cardStruct = new CardStruct();
            cardStruct.type = 1;
            cardStruct.dan = game.getPlayerCardInfos().get(game.getPlayTurn()).MinimumCards();
            cardStruct.cards = game.getPlayerCardInfos().get(game.getPlayTurn()).MinimumCards();
            cardStruct.setUserId(game.getPlayTurn());

            String roomId = game.getRoom().getRoomId();
            int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
            KafkaMsgKey msgKey = new KafkaMsgKey();
            msgKey.setUserId(game.getPlayTurn());
            msgKey.setRoomId(roomId);
            msgKey.setPartition(partition);

            //{"service":"gameService","method":"play","params":{"cards":{"userId":"5","cards":[12],"type":1,"dan":[12]}}}

            Map<String, Object> put = new HashMap<>();
            put.put("cards", cardStruct);
            ResponseRobotVo result = new ResponseRobotVo("gameService", "play",put);
            SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
        } else {
            pass(game);
        }
    }

    @Override
    public void pass(GameDouDiZhu game) {
        String roomId = game.getRoom().getRoomId();
        int partition = SpringUtil.getBean(ServerConfig.class).getServerId();
        KafkaMsgKey msgKey = new KafkaMsgKey();
        msgKey.setUserId(game.getPlayTurn());
        msgKey.setRoomId(roomId);
        msgKey.setPartition(partition);
        //按service的名字 分topic
        //{"service":"gameService","method":"pass","params":{"userId":"8"}}
        Map<String, Object> put = new HashMap<>();
        put.put("userId",game.getPlayTurn());
        ResponseRobotVo result = new ResponseRobotVo("gameService", "pass",put);
        SpringUtil.getBean(MsgProducer.class).send2Partition("gameService",partition, msgKey, result);
    }
}
