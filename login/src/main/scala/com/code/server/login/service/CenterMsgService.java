package com.code.server.login.service;

import com.code.server.constant.club.RoomModel;
import com.code.server.constant.game.Record;
import com.code.server.constant.game.RoomRecord;
import com.code.server.constant.game.UserBean;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.*;
import com.code.server.db.model.*;
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
    private static ClubRecordService clubRecordService = SpringUtil.getBean(ClubRecordService.class);
    private static AgentUserService agentUserService = SpringUtil.getBean(AgentUserService.class);

    private static ReplayService replayService = SpringUtil.getBean(ReplayService.class);

    private static UserService userService = SpringUtil.getBean(UserService.class);


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
            case KAFKA_MSG_ID_REFRESH_ROOM_INSTANCE:
                refreshRoomInstance(msg);
                break;
            case KAFKA_MSG_ID_ROOM_CLUB_USER:
                getRoomClubByUser(msg);


        }
    }

    private static void refreshRoomInstance(String msg){

        JsonNode jsonNode = JsonUtil.readTree(msg);
        String clubId = jsonNode.path("clubId").asText();
        String roomModelId = jsonNode.path("roomModelId").asText();
        String roomId = jsonNode.path("roomId").asText();
        Club club = ClubManager.getInstance().getClubById(clubId);
        if (club != null) {
            GameClubService.initRoomInstance(club);
        }
    }

    private static void getRoomClubByUser(String msg) {
        JsonNode jsonNode = JsonUtil.readTree(msg);

        Map<String, Object> map = JsonUtil.readValue(msg, new TypeReference<HashMap<String, Object>>() {});

        Map<String, Object> result = JsonUtil.readValue(msg, Map.class);
        Long userId = jsonNode.path("userId").asLong();
        List<String> clubs = ClubManager.getInstance().getUserClubs(userId);
        map.put("clubs", clubs);

        GameClubService.sendMsg2Player(new ResponseVo("roomService","getRoomClubByUser",map),userId);

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

    private static void genGameRecord(String msg) {
        if (msg != null) {
            JsonNode jsonNode = JsonUtil.readTree(msg);
            Map<String, Object> map = JsonUtil.readValue(msg, new TypeReference<HashMap<String, Object>>() {
            });

            long room_uuid = (Long) map.get("room_uuid");
            long replay_id = (Long) map.get("replay_id");
            int count = (int) map.get("count");
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

    private static void genRoomRecord(String msg) {
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

        //俱乐部战绩
        String clubId = roomRecord.getClubId();
        if (clubId != null && !"".equals(clubId)) {
            Club club = ClubManager.getInstance().getClubById(clubId);
            if (club != null) {
                String roomModel = roomRecord.getClubRoomModel();
                RoomModel rm = GameClubService.getRoomModel(club, roomModel);
                roomRecord.setName(rm.getDesc());
            }
            clubRecordService.addRecord(clubId, roomRecord);
        }
    }


    private static void guessAddGold(String msg) {
        if (msg != null) {
            JsonNode jsonNode = JsonUtil.readTree(msg);
            int userId = jsonNode.path("userId").asInt();
            double gold = jsonNode.path("gold").asDouble();
            UserBean own = RedisManager.getUserRedisService().getUserBean(userId);
            int bindUser1 = own.getReferee();


            double addGold = gold * 4;

            AgentUser agentUser = agentUserService.getAgentUserDao().findAgentUserByInvite_code(""+bindUser1);
            if (agentUser != null) {
                agentUser.setGold(agentUser.getGold() + addGold);
                agentUserService.getAgentUserDao().save(agentUser);
            }




//            //第一级代理
//            if (bindUser1 != 0) {
//                UserBean userBean1 = loadUserBean(bindUser1);
//                if (userBean1.getId() == 1) {//是总代理
//                    RedisManager.getUserRedisService().addUserGold(bindUser1, gold * 3);
//                } else {
//                    RedisManager.getUserRedisService().addUserGold(bindUser1, gold);
//
//                    //第二级代理
//                    int bindUser2 = userBean1.getReferee();
//                    if (bindUser2 != 0) {
//                        UserBean userBean2 = loadUserBean(bindUser2);
//                        if (userBean2.getId() == 1) {//是总代理
//                            RedisManager.getUserRedisService().addUserGold(bindUser2, gold * 2);
//                        } else {
//                            RedisManager.getUserRedisService().addUserGold(bindUser2, gold);
//
//                            //第三级代理
//                            int bindUser3 = userBean2.getReferee();
//
//                            if (bindUser3 != 0) {
//                                UserBean userBean3 = loadUserBean(bindUser3);
//                                if (userBean3.getId() == 1) {//是总代理
//                                    RedisManager.getUserRedisService().addUserGold(bindUser3, gold * 1);
//                                } else {
//                                    RedisManager.getUserRedisService().addUserGold(bindUser3, gold);
//
//                                }
//                            }
//
//                        }
//                    }
//                }
//                //给总代理2份
//                RedisManager.getUserRedisService().addUserGold(1, gold * 2);
//
//            }
        }
    }

    private static UserBean loadUserBean(long userId) {
        UserBean userBean = RedisManager.getUserRedisService().getUserBean(userId);
        if (userBean == null) {
            User user = userService.getUserByUserId(userId);
            LoginAction.saveUser2Redis(user, LoginAction.getToken(userId));
            userBean = RedisManager.getUserRedisService().getUserBean(userId);
        }
        return userBean;

    }


}
