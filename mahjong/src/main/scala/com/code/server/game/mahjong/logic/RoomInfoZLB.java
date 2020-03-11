package com.code.server.game.mahjong.logic;

import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.game.room.service.RoomManager;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;
import com.code.server.util.timer.GameTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2019-07-09.
 */
public class RoomInfoZLB extends RoomInfo {


    @Override
    public boolean isGoldRoom() {
        return false;
    }

    @Override
    public int joinRoom(long userId, boolean isJoin, int... seat) {
        //随机匹配的金币房

        int rtn = super.joinRoom(userId, isJoin, seat);
        if (rtn != 0) {
            return rtn;
        }

        //如果房间已满 加入已满房间
        if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT && this.isRoomFull()) {
            RoomManager.getInstance().moveGoldRoomNotFull2Full(this);
        }

//            getReady(userId);
        GameTimer.addTimerNode(2000, false, () -> getReady(userId));
        return 0;


    }

    public void genRoomRecord() {
        if (!isOpen) return;
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.setRoomId(this.roomId);
        roomRecord.setId(this.getUuid());
        roomRecord.setType(this.roomType);
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setClubId(clubId);
        roomRecord.setClubRoomModel(clubRoomModel);
        roomRecord.setGameType(gameType);
        roomRecord.setModelTotal(modeTotal);
        roomRecord.setMode(mode);
        roomRecord.setCurGameNum(curGameNumber);

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ROOM_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);

        this.userScores.forEach((key, value) -> {
            UserRecord userRecord = new UserRecord();
            userRecord.setScore(value);
            userRecord.setUserId(key);
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(key);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            roomRecord.getRecords().add(userRecord);


            Map<String, Object> n = new HashMap<>();
            n.put("userId", key);
            n.put("num", 1);
            KafkaMsgKey kafkaMsgKey1 = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ADD_COUPON);

            msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey1, n);
        });


        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);


        double maxScore = this.userScores.values().stream().max(Double::compare).get();
        List<Long> maxList = new ArrayList<>();
        this.userScores.forEach((uid,score)->{
            if (score == maxScore) {
                maxList.add(uid);
            }
        });

        int giveNum = getWinGiveMoney(maxList.size());

        if (this.curGameNumber != 1 || maxList.size() != 4) {

            maxList.forEach(uid->{
                RedisManager.getUserRedisService().addUserMoney(uid, giveNum);
                //胜场数+1

                Map<String, Object> n = new HashMap<>();
                n.put("userId", uid);
                n.put("num", 1);
                KafkaMsgKey kafkaMsgKey1 = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_ADD_WIN_NUM);

                msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey1, n);
            });
        }

    }

    private int getWinGiveMoney(int num) {
        return (this.personNumber * 2 -2) /num;
    }

    public static void main(String[] args) {
        Map<Integer, Double> map = new HashMap<>();
        map.put(1, 1D);
        map.put(1, 1D);
        map.put(1, 1D);
        double maxScore = map.values().stream().max(Double::compare).get();
        System.out.println(maxScore);
    }

    @Override
    public int quitRoom(long userId) {

        if (!this.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_NOT_EXIST;
        }

        if (isInGame && this.game.users.contains(userId)) {
            return ErrorCode.CANNOT_QUIT_ROOM_IS_IN_GAME;
        }

//            List<Long> noticeList = new ArrayList<>();
//            noticeList.addAll(this.getUsers());

        //删除玩家房间映射关系
        roomRemoveUser(userId);


        if (goldRoomPermission == GOLD_ROOM_PERMISSION_DEFAULT) {
            RoomManager.getInstance().moveFull2NotFullRoom(this);
        }

        //todo 如果都退出了  删除房间
        if (this.users.size() == 0) {
            RoomManager.removeRoom(this.roomId);
        }
        noticeQuitRoom(userId);
        return 0;

    }

    public void drawBack() {
        //do nothing
    }
}
