package com.code.server.login.service;

import com.code.server.constant.game.Record;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.db.Service.GameRecordService;
import com.code.server.db.Service.ReplayService;
import com.code.server.db.Service.UserRecordService;
import com.code.server.db.Service.UserService;
import com.code.server.db.model.GameRecord;
import com.code.server.db.model.Replay;
import com.code.server.db.model.User;
import com.code.server.db.model.UserRecord;
import com.code.server.login.action.LoginAction;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunxianping on 2017/6/16.
 */
public class CenterMsgService implements IkafkaMsgId {


    private static UserRecordService userRecordService = SpringUtil.getBean(UserRecordService.class);
    private static GameRecordService gameRecordService = SpringUtil.getBean(GameRecordService.class);

    private static ReplayService replayService = SpringUtil.getBean(ReplayService.class);

    private static UserService userService =  SpringUtil.getBean(UserService.class);

    public static void dispatch(KafkaMsgKey msgKey, String msg) {
        int msgId = msgKey.getMsgId();
        switch (msgId) {
            case KAFKA_MSG_ID_GEN_RECORD:
                genRecord(msg);
                break;
            case KAFKA_MSG_ID_REPLAY:
                replay(msg);
                break;
            case KAFKA_MSG_ID_GAME_RECORD:
                genGameRecord(msg);
                break;
            case KAFKA_MSG_ID_ROOM_RECORD:
                genRoomRecord(msg);
                break;
            case KAFKA_MSG_ID_GUESS_ADD_GOLD:
                guessAddGold(msg);
                break;


        }
    }

    private static void genRecord(String msg) {
        RoomRecord roomRecord = JsonUtil.readValue(msg, RoomRecord.class);

        List<com.code.server.constant.game.UserRecord> lists = roomRecord.getRecords();
        for (com.code.server.constant.game.UserRecord userRecord : lists) {
            UserRecord addRecord = userRecordService.getUserRecordByUserId(userRecord.getUserId());
            if (addRecord != null) {
                userRecordService.addRecord(userRecord.getUserId(), roomRecord);
            } else {
                Record record = new Record();
                record.addRoomRecord(roomRecord);

                UserRecord newRecord = new UserRecord();
                newRecord.setId(userRecord.getUserId());
                newRecord.setRecord(record);
                userRecordService.save(newRecord);
            }
        }
    }

    private static void replay(String msg) {
        if (msg != null) {
            long id = JsonUtil.readTree(msg).path("id").asLong();
            int count = JsonUtil.readTree(msg).path("count").asInt();
            long room_uuid = JsonUtil.readTree(msg).path("room_uuid").asLong();
            Replay replay = new Replay();
            replay.setId(id);
            replay.setLeftCount(count);
            replay.setData(msg);
            replay.setRoomUuid(room_uuid);
            replayService.save(replay);
        }

    }
    private static void genGameRecord(String msg){
        if (msg != null) {
            System.out.println(msg);
            JsonNode jsonNode = JsonUtil.readTree(msg);
            Map<String,Object> map = JsonUtil.readValue(msg, new TypeReference<HashMap<String,Object>>() {});

            long room_uuid = (Long)map.get("room_uuid");
            long replay_id = (Long)map.get("replay_id");
            int count = (int)map.get("count");
            String recordStr = jsonNode.path("record").asText();
            System.out.println(recordStr);
            Gson gson = new Gson();
            com.code.server.constant.game.GameRecord data = gson.fromJson(recordStr, com.code.server.constant.game.GameRecord.class);
//            com.code.server.constant.game.GameRecord data = (com.code.server.constant.game.GameRecord)map.get("record");
            GameRecord gameRecord = new GameRecord();
            gameRecord.setDate(new Date());
            gameRecord.setUuid(room_uuid);
            gameRecord.setLeftCount(count);
            gameRecord.setGameRecord(data);
            gameRecord.setReplayId(replay_id);
            gameRecordService.gameRecordDao.save(gameRecord);
        }
    }

    private static void genRoomRecord(String msg){
        RoomRecord roomRecord = JsonUtil.readValue(msg, RoomRecord.class);

        List<com.code.server.constant.game.UserRecord> lists = roomRecord.getRecords();
        for (com.code.server.constant.game.UserRecord userRecord : lists) {
            UserRecord addRecord = userRecordService.getUserRecordByUserId(userRecord.getUserId());
            if (addRecord != null) {
                userRecordService.addRecord(userRecord.getUserId(), roomRecord);
            } else {
                Record record = new Record();
                record.addRoomRecord(roomRecord);

                UserRecord newRecord = new UserRecord();
                newRecord.setId(userRecord.getUserId());
                newRecord.setRecord(record);
                userRecordService.save(newRecord);
            }
        }
    }


    private static void guessAddGold(String msg) {
        if (msg != null) {
            JsonNode jsonNode = JsonUtil.readTree(msg);
            long userId = jsonNode.path("userId").asLong();
            double gold = jsonNode.path("gold").asDouble();
            UserBean userBean1 = RedisManager.getUserRedisService().getUserBean(userId);
            if (userBean1 == null) {
                User user = userService.getUserByUserId(userId);
                LoginAction.saveUser2Redis(user, LoginAction.getToken(userId));
            }



        }
    }


}
