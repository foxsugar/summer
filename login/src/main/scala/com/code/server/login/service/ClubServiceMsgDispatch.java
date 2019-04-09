package com.code.server.login.service;

import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.kafka.MsgProducer;
import com.code.server.login.config.ServerConfig;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by sunxianping on 2018/1/15.
 */
@Service
public class ClubServiceMsgDispatch {


//        @Autowired
    GameClubService gameClubService;

//    @Autowired
//    GameClubHasMoneyService gameClubHasMoneyService;


    @Autowired
    MsgProducer kafkaMsgProducer;


    public void dispatchMsg(KafkaMsgKey msgKey, JsonNode params) {
        String service = params.get("service").asText();
        String method = params.get("method").asText();
        JsonNode node = params.get("params");
        int rtn = dispatchUserService(msgKey, method, node, params);
        if (rtn != 0) {
            ResponseVo vo = new ResponseVo(service, method, rtn);
            kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(), "" + msgKey.getUserId(), vo);
        }
    }


    private int dispatchUserService(KafkaMsgKey msgKey, String method, JsonNode params, JsonNode allParams) {
        //todo 放开
        boolean hasClubMoney = SpringUtil.getBean(ServerConfig.class).getHasClubMoney() == 1;
        if (hasClubMoney) {
            gameClubService = SpringUtil.getBean(GameClubHasMoneyService.class);
        } else {
            gameClubService = (GameClubService)SpringUtil.getBean("gameClubService");
        }
        long userId = msgKey.getUserId();
        String clubId = params.path("clubId").asText();
        boolean clubHasMoney = params.path("clubHasMoney").asBoolean(false);
        switch (method) {
            case "lookClub":
                return gameClubService.lookClub(msgKey, userId);
            case "getClubInfo":
                return gameClubService.getClubInfo(msgKey, userId, clubId);
            case "createClub":
                String clubName = params.get("clubName").asText();
                String wx = params.get("wx").asText();
                String area = params.get("area").asText();
                String desc = params.get("desc").asText();
                return gameClubService.createClub(msgKey, userId, clubName, wx, area, desc);
            case "setClub":
                String clubName_set = params.get("clubName").asText();
                String wx_set = params.get("wx").asText();
                String area_set = params.get("area").asText();
                String desc_set = params.get("desc").asText();
                return gameClubService.setClub(msgKey, userId, clubId, clubName_set, wx_set, area_set, desc_set);
            case "markUser":
                long markUser = params.get("markUser").asLong();
                String mark1 = params.get("mark").asText();
                return gameClubService.markUser(msgKey, userId, clubId, markUser, mark1);
            case "dissolve":
                return gameClubService.dissolve(msgKey, userId, clubId);
            case "isHasClub":
                return gameClubService.isHasClub(msgKey, userId, clubId);
            case "joinClub":
                String mark = params.get("mark").asText();
                return gameClubService.joinClub(msgKey, userId, clubId, mark);
            case "partnerRecommend":
                long recommendId = params.path("recommendId").asLong();
                return gameClubService.partnerRecommend(msgKey, userId, clubId, recommendId);
            case "quitClub":
                return gameClubService.quitClub(msgKey, userId, clubId);
            case "agree":
                long agreeUser = params.get("applyUserId").asLong();
                boolean isAgree = params.get("isAgree").asBoolean();
                return gameClubService.agree(msgKey, userId, clubId, agreeUser, isAgree);

            case "charge":
                int money = params.get("money").asInt();
                return gameClubService.charge(msgKey, userId, clubId, money);

            case "createRoomModel": {

                String createCommand = params.get("createCommand").asText();
                String gameType = params.get("gameType").asText();
                int gameNumber = params.get("gameNumber").asInt();
                String desc1 = params.get("desc").asText();
                String str = params.path("indexs").toString();
                List<Integer> indexs = null;
                if (str != null && !str.equals("")) {
                    indexs = JsonUtil.readValue(params.path("indexs").toString(), new TypeReference<List<Integer>>() {
                    });
                }

                return gameClubService.createRoomModel(msgKey, userId, clubId, createCommand, gameType, gameNumber, desc1, indexs);
            }

            case "removeRoomModel":
                String roomModelId = params.get("roomModelId").asText();
                return gameClubService.removeRoomModel(msgKey, userId, clubId, roomModelId);
            case "setRoomModel": {

                String createCommand_set = params.get("createCommand").asText();
                String gameType_set = params.get("gameType").asText();
                int gameNumber_set = params.get("gameNumber").asInt();
                String desc1_set = params.get("desc").asText();
                String roomModelId_set = params.get("roomModelId").asText();
                return gameClubService.setRoomModel(msgKey, userId, clubId, roomModelId_set, createCommand_set, gameType_set, gameNumber_set, desc1_set);
            }

            case "setRoomModelBatch": {
                String createCommand_set = params.get("createCommand").asText();
                String gameType_set = params.get("gameType").asText();
                int gameNumber_set = params.get("gameNumber").asInt();
                String desc1_set = params.get("desc").asText();
                List<Integer> indexs = JsonUtil.readValue(params.path("indexs").toString(), new TypeReference<List<Integer>>() {
                });
                return gameClubService.setRoomModelBatch(msgKey, userId, clubId, createCommand_set, gameType_set, gameNumber_set, desc1_set, indexs);
            }

            case "clubRoomSetId":

                String clubModelId = params.get("clubModelId").asText();
                String roomId = params.get("roomId").asText();
                return gameClubService.clubRoomSetId(clubId, clubModelId, roomId);

            case "clubGameStart":
                String clubModelId1 = params.get("clubModelId").asText();
                String us = params.get("users").toString();
                int gameNumber = params.get("curGameNumber").asInt();
                String roomIdc = params.get("roomId").asText();
                List<Long> users = JsonUtil.readValue(us, new TypeReference<List<Long>>() {
                });
//                String roomId = params.get("roomId").asText();
                return gameClubService.cludGameStart(clubId, clubModelId1, users, roomIdc, gameNumber);
            case "getFreeUser":
                return gameClubService.getFreeUser(msgKey, clubId);
            case "invite":
                String inviteUser = params.get("inviteUser").asText();
                String roomId1 = params.get("roomId").asText();
                String roomModel = params.get("roomModel").asText();
                String name = params.get("name").asText();
                int type = params.path("type").asInt(0);
                return gameClubService.invite(msgKey, clubId, roomId1, inviteUser, roomModel, name, type);

            case "getClubRecord":
                return gameClubService.getClubRecord(msgKey, userId, clubId);
            case "getClubRecordByDate":
                String date = params.get("date").asText();
                return gameClubService.getClubRecordByDate(msgKey, userId, clubId, date);
            case "clubDrawBack":
                String clubModelId2 = params.get("clubModelId").asText();
                String rid1 = params.path("roomId").asText();
                int mode = params.path("clubMode").asInt();
                return gameClubService.clubDrawBack(clubId, clubModelId2, rid1, mode);
            case "kickUser":
                long kickUser = params.get("kickUserId").asLong();
                return gameClubService.kickUser(msgKey, userId, clubId, kickUser);

            case "getChargeRecord":
                return gameClubService.getChargeRecord(msgKey, userId, clubId);

            case "setFloorDesc": {

                int floor = params.get("floor").asInt();
                String desc1 = params.get("desc").asText();
                return gameClubService.setFloor(msgKey, userId, clubId, floor, desc1);
            }

            case "setAdmin": {
                long adminUser = params.get("adminUser").asLong();
                boolean isAdd = params.get("isAdd").asBoolean();
                return gameClubService.setAdmin(msgKey, userId, clubId, adminUser, isAdd);
            }
            case "addUser": {
                long user = params.get("userId").asLong();
                //todo 对协议 新增字段
                long referee = params.path("referee").asLong(0);
                return gameClubService.addUser(msgKey, clubId, user, referee);
            }
            case "removeFloor": {
                int floor = params.get("floor").asInt();
                return gameClubService.removeFloor(msgKey, clubId, userId, floor);
            }
            case "clubJoinRoom": {
                clubId = params.path("clubId").asText();
                String clubModelId3 = params.path("clubModelId").asText();
                String roomId2 = params.path("roomId").asText();
                long joinUser = params.path("userId").asLong();


                gameClubService.clubJoinRoom(clubId, joinUser, clubModelId3, roomId2);
                break;
            }
            case "clubQuitRoom": {
                clubId = params.path("clubId").asText();
                String clubModelId3 = params.path("clubModelId").asText();
                String roomId3 = params.path("roomId").asText();
                long quitUser = params.path("userId").asLong();

                gameClubService.clubQuitRoom(clubId, quitUser, clubModelId3, roomId3);
                break;
            }
            case "setAutoJoin": {
                boolean auto = params.path("auto").asBoolean();
                return gameClubService.setAutoJoin(msgKey, clubId, userId, auto);
            }

            case "removeClubInstance": {
                clubId = params.path("clubId").asText();
                String clubModelId4 = params.path("clubModelId").asText();
                String rid = params.path("roomId").asText();
                gameClubService.removeClubInstance(clubId, clubModelId4, rid);
                break;
            }

            case "transfer": {
                long toUser = params.path("toUser").asLong();
                return gameClubService.transfer(msgKey, clubId, userId, toUser);
            }

            case "setPartner": {
                long partnerId = params.path("partnerId").asLong();
                return gameClubService.setPartner(msgKey, clubId, userId, partnerId);
            }

            case "removePartner": {
                long partnerId = params.path("partnerId").asLong();
                return gameClubService.removePartner(msgKey, clubId, userId, partnerId);
            }
            case "changePartner": {
                long newPartner = params.path("newPartner").asLong();
                long changeUser = params.path("changeUser").asLong();
                return gameClubService.changePartner(msgKey, clubId, userId, newPartner, changeUser);

            }

            case "upScore": {
                long toUser = params.path("toUser").asLong();
                int num = params.path("num").asInt();
                return gameClubService.upScore(msgKey, clubId, userId, toUser, num);
            }

            case "getUpScoreLog": {
                return gameClubService.getUpScoreLog(msgKey, clubId, userId);
            }

            case "createInstance":{
                String roomModelId1 = params.path("roomModelId").asText();
                return gameClubService.createInstance(msgKey, clubId, userId, roomModelId1);
            }

            case "getUserByPartner":{
                long partnerId = params.get("partnerId").asLong();
                return gameClubService.getUserByPartner(msgKey, clubId, userId, partnerId);
            }

            case "getPartner":{
                return gameClubService.getPartner(msgKey, clubId);

            }

            case "getClubAdmin":{
                return gameClubService.getClubAdmin(msgKey, clubId);
            }
            case "setCreditInfo":{
                int creditMode = params.path("creditMode").asInt();
                int creditMin = params.path("creditMin").asInt();
                int dayingjia = params.path("dayingjia").asInt();
                int aa = params.path("aa").asInt();
                boolean only = params.path("only").asBoolean();

                return gameClubService.setCreditInfo(msgKey, clubId, creditMode, creditMin, only,dayingjia, aa);

            }

            case "setCreditScore":{
                long toUser = params.path("toUser").asLong();
                int score = params.path("score").asInt();
                boolean clear = params.path("clear").asBoolean(false);
                return gameClubService.setCreditScore(msgKey, clubId,toUser, score, clear);
            }

            case "clearAllMemberCredit":{
                int type1 = params.path("type").asInt(0);
                return gameClubService.clearAllMemberCredit(msgKey, clubId, type1);
            }


        }
        return 0;
    }
}
