package com.code.server.game.room;


import com.code.server.constant.game.Record;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.IfaceGameVo;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.SpringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class Game implements IfaceGame{

    public List<Long> users = new ArrayList<>();
    public int number;

    public void startGame(List<Long> users,Room room){

    }

    protected void genRecord() {

    }
    /**
     * 下个人
     *
     * @param curId
     * @return
     */
    protected long nextTurnId(long curId) {
        int index = users.indexOf(curId);

        int nextId = index + 1;
        if (nextId >= users.size()) {
            nextId = 0;
        }
        return users.get(nextId);
    }

    protected void genRecord(Map<Long,Double> scores, Room room,long id) {

        Record.RoomRecord roomRecord = new Record.RoomRecord();
        roomRecord.setTime(System.currentTimeMillis());
        roomRecord.setType(room.getRoomType());
        roomRecord.setId(id);
        scores.entrySet().forEach((playerInfo) -> {
            Record.UserRecord userRecord = new Record.UserRecord();
            userRecord.setScore(playerInfo.getValue());
            userRecord.setUserId(playerInfo.getKey());
            userRecord.setRoomId(room.getRoomId());
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(playerInfo.getKey());
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            roomRecord.addRecord(userRecord);
        });

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_GEN_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, roomRecord);
    }

    @Override
    public IfaceGameVo toVo() {
        return null;
    }

    @Override
    public IfaceGameVo toVo(long watchUser) {
        return null;
    }

    public List<Long> getUsers() {
        return users;
    }

    public Game setUsers(List<Long> users) {
        this.users = users;
        return this;
    }
}
