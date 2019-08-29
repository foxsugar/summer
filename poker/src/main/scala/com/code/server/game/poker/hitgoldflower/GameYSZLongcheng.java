package com.code.server.game.poker.hitgoldflower;

import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.poker.zhaguzi.*;
import com.code.server.game.poker.zhaguzi.PokerItem;
import com.code.server.game.room.Room;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.List;

/**
 * Created by sunxianping on 2019-06-05.
 */
public class GameYSZLongcheng extends GameYSZ {




    public void startGame(List<Long> users, Room room) {

        com.code.server.game.poker.zhaguzi.PokerItem.isYSZ = true;

        this.room = (RoomYSZLongcheng) room;
        init(users);
        updateLastOperateTime();
        //通知其他人游戏已经开始
        MsgSender.sendMsg2Player(new ResponseVo("gameService", "gameBegin", "ok"), this.getUsers());
    }


    /**
     * 生成战绩
     */
    public void genRoomRecord(){

        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setRoomId(this.room.getRoomId());
        roomRecord.setId(this.room.getUuid());
        roomRecord.setType(this.room.getRoomType());
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setGameType(this.room.getGameType());
        roomRecord.setCurGameNum(this.room.curGameNumber);
        roomRecord.setAllGameNum(this.room.getGameNumber());
        roomRecord.setOpen(this.room.isOpen);

        this.playerCardInfos.values().forEach(playerInfo->{
            UserRecord userRecord = new UserRecord();
            if (playerInfo.getFinalScore() > 0) {
                userRecord.setScore(playerInfo.getFinalScore() - playerInfo.getAllScore());
            }else{
                userRecord.setScore(playerInfo.getFinalScore());
            }
            userRecord.setUserId(playerInfo.userId);
            roomRecord.addRecord(userRecord);

            KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(IkafkaMsgId.KAFKA_MSG_ID_ROOM_RECORD);
            MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
            msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);
        });


    }


    /**
     * 战绩
     */
    protected void genRecord() {
        genRoomRecord();
    }
    /**
     * 算分
     *
     * @param winList
     */
    protected void compute(List<Long> winList) {
        RoomYSZ roomYSZ = null;
        if (room instanceof RoomYSZ) {
            roomYSZ = (RoomYSZ) room;
        }
        //设置每个人的牌类型
        for (PlayerYSZ playerCardInfo : playerCardInfos.values()) {
            com.code.server.game.poker.zhaguzi.Player p = new com.code.server.game.poker.zhaguzi.Player(playerCardInfo.getUserId(), ArrUtils.cardCode.get(playerCardInfo.getHandcards().get(0)), ArrUtils.cardCode.get(playerCardInfo.getHandcards().get(1)), ArrUtils.cardCode.get(playerCardInfo.getHandcards().get(2)));
            playerCardInfo.setCardType(p.getCategory().toString());
            if (PokerItem.is235(p.getPokers())) {
                playerCardInfo.setCardType("BaoZiShaShou");
            }

            //添加次数
            if ("BaoZi".equals(playerCardInfo.getCardType())) {
                logger.info("");
                roomYSZ.addBaoziNum(playerCardInfo.getUserId());
            } else if ("ShunJin".equals(playerCardInfo.getCardType())) {
                logger.info("");
                roomYSZ.addTonghuashunNum(playerCardInfo.getUserId());
            } else if ("JinHua".equals(playerCardInfo.getCardType())) {
                logger.info("");
                roomYSZ.addTonghuaNum(playerCardInfo.getUserId());
            } else if ("ShunZi".equals(playerCardInfo.getCardType())) {
                logger.info("");
                roomYSZ.addShunziNum(playerCardInfo.getUserId());
            } else if ("DuiZi".equals(playerCardInfo.getCardType())) {
                roomYSZ.addDuiziNum(playerCardInfo.getUserId());
            } else if ("DanZi".equals(playerCardInfo.getCardType())) {
                roomYSZ.addSanpaiNum(playerCardInfo.getUserId());
            }
        }
//        //添加彩分
//        for (PlayerYSZ playerCardInfo : playerCardInfos.values()) {
//            if ("BaoZi".equals(playerCardInfo.getCardType())) {
//                double tempCaifen = 0.0;
//                for (PlayerYSZ p : playerCardInfos.values()) {
//                    if (playerCardInfo.getUserId() != p.getUserId()) {
//                        tempCaifen += room.getCaiFen();
//                        p.setCaifen(p.getCaifen() - room.getCaiFen());
//                    }
//                }
//                playerCardInfo.setCaifen(playerCardInfo.getCaifen() + tempCaifen);
//            }
//        }

        //算分
        double totalChip = 0.0;
        for (PlayerYSZ playerCardInfo : playerCardInfos.values()) {
            totalChip += playerCardInfo.getAllScore();
        }
        for (PlayerYSZ playerCardInfo : playerCardInfos.values()) {
            if (winList.contains(playerCardInfo.getUserId())) {
                //抽水10%
                double add = totalChip * 95 /100 /winList.size();

                playerCardInfo.setScore(add);

            } else {
                playerCardInfo.setScore(-1 * playerCardInfo.getAllScore());
            }

            //todo 返利

            double rebate = 3D / 100 * 70 / 100 * totalChip / playerCardInfos.size() + 3D / 100 * 30 / 100 * (playerCardInfo.getAllScore()) ;

            this.room.sendCenterAddRebateLongcheng(playerCardInfo.getUserId(), rebate);
        }
        this.room.sendCenterAddRebateLongcheng(10005, totalChip * 2 /100);
        for (PlayerYSZ playerCardInfo : playerCardInfos.values()) {
            if (winList.contains(playerCardInfo.getUserId())) {
                logger.info("");
                room.addUserSocre(playerCardInfo.getUserId(), playerCardInfo.getScore());
//                room.addUserSocre(playerCardInfo.getUserId(), playerCardInfo.getCaifen());
                playerCardInfo.setFinalScore(playerCardInfo.getScore());
            } else {
//                room.addUserSocre(playerCardInfo.getUserId(), -1 * playerCardInfo.getAllScore());
//                room.addUserSocre(playerCardInfo.getUserId(), playerCardInfo.getCaifen());
                playerCardInfo.setFinalScore(-1 * playerCardInfo.getAllScore());
            }
        }
    }

}
