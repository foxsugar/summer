package com.code.server.game.room;


import com.code.server.constant.game.GameRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.game.UserRecord;
import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.IfaceGameVo;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/3/14.
 */
public class Game implements IfaceGame{

    public List<Long> users = new ArrayList<>();
    public int number;
    public long lastOperateTime;

    public void startGame(List<Long> users,Room room){

    }

    protected void genRecord() {

    }

    public void updateLastOperateTime() {
        this.lastOperateTime = System.currentTimeMillis();
    }


    /**
     * 给所有人推送 包括观战的人
     *
     * @param responseVo
     */
    protected void pushToAll(ResponseVo responseVo) {
        List<Long> allUser = new ArrayList<>();
        allUser.addAll(users);
        allUser.addAll(this.getRoom().watchUser);
        MsgSender.sendMsg2Player(responseVo, allUser);
    }


    /**
     * 给所有人推送 包括观战的人
     *
     *
     */
    protected void pushToAll(String service,String method, Object params) {
        ResponseVo responseVo = new ResponseVo(service, method, params);
        List<Long> allUser = new ArrayList<>();
        allUser.addAll(users);
        allUser.addAll(this.getRoom().watchUser);
        MsgSender.sendMsg2Player(responseVo, allUser);
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

        Map<String, Object> data = new HashMap<>();
        data.put("count", scores.size());
        data.put("room_uuid", room.getUuid());
        data.put("replay_id", id);


        GameRecord gameRecord = new GameRecord();
        gameRecord.setCurGameNumber(room.getCurGameNumber());



        scores.forEach((key, value) -> {
            UserRecord userRecord = new UserRecord();
            userRecord.setScore(value);
            userRecord.setUserId(key);
            userRecord.setRoomId(room.getRoomId());
            UserBean userBean = RedisManager.getUserRedisService().getUserBean(key);
            if (userBean != null) {
                userRecord.setName(userBean.getUsername());
            }
            gameRecord.getRecords().add(userRecord);
        });
        data.put("record", JsonUtil.toJson(gameRecord));

        KafkaMsgKey kafkaMsgKey = new KafkaMsgKey().setMsgId(KAFKA_MSG_ID_GAME_RECORD);
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);
        msgProducer.send(IKafaTopic.CENTER_TOPIC, kafkaMsgKey, data);
    }

    public boolean isCanChangeRoom(long userId) {
        return !users.contains(userId);
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

    public long getLastOperateTime() {
        return lastOperateTime;
    }

    public Game setLastOperateTime(long lastOperateTime) {
        this.lastOperateTime = lastOperateTime;
        return this;
    }

    public Room getRoom(){
        return null;
    }
}
