package com.code.server.game.poker.doudizhu;


import com.code.server.constant.game.CardStruct;
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
 * Created by clark on 2017/5/16.
 */
public class DouDiZhuGoldRobot implements IDouDiZhuRobot,IGameConstant {

    @Override
    public void execute() {
//        RoomManager.getInstance().getFullGoldRoom().values().forEach(list->list.forEach());
       RoomManager.getInstance().getRobotRoom().forEach(this::doExecute);
        //RoomManager.getRobotRoom().forEach(this::doExecute);
    }

    public void doExecute(Room room) {
        if (room == null || room.getGame() == null) {
            return;
        }
        if (room.getGame() instanceof GameDouDiZhuGold) {
            GameDouDiZhu game = (GameDouDiZhu) room.getGame();
            long now = System.currentTimeMillis();
            //执行
            if(now > game.lastOperateTime + SECOND * 20){
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
            }
        }
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
