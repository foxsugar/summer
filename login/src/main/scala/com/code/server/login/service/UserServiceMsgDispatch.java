package com.code.server.login.service;

import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.code.server.db.Service.ReplayService;
import com.code.server.kafka.MsgProducer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2017/5/31.
 */
@Service
public class UserServiceMsgDispatch {

    @Autowired
    GameUserService gameUserService;

    @Autowired
    MsgProducer kafkaMsgProducer;

    @Autowired
    ReplayService replayService;

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

//        String method = params.get("method").asText();
        switch (method) {

            case "getUserMessage": {
                return gameUserService.getUserMessage(msgKey);
            }
            case "getUserRecodeByUserId": {
                String roomType = params.get("roomType").asText();
                return gameUserService.getUserRecodeByUserId(msgKey,roomType);
            }
            case "bindReferrer": {//邀请码
                int referrerId = params.get("referrerId").asInt();
                return gameUserService.bindReferrer(msgKey, referrerId);

            }
            case "giveOtherMoney":

                double money = params.get("money").asDouble();
                Long rechargeUserId = params.get("userId").asLong();
                return gameUserService.giveOtherMoney(msgKey, rechargeUserId, money);
            case "giveOtherGold":
                double gold = params.get("gold").asDouble();
                Long rechargeUserId1 = params.get("userId").asLong();
                return gameUserService.giveOtherGold(msgKey, rechargeUserId1, gold);

            case "getNickNamePlayer":
                return gameUserService.getNickNamePlayer(msgKey);
            case "getOtherPlayerInfo":
                long otherPlayerId = params.path("userId").asLong();
                return gameUserService.getOtherPlayerInfo(msgKey,otherPlayerId);
            case "getOnlinePeople":
                return gameUserService.getOnlinePeople(msgKey);
            case "getServerInfo":
                return gameUserService.getServerInfo(msgKey);
            case "reportingCoord":
                String coord = params.get("coord").asText();
                return gameUserService.reportingCoord(msgKey,coord);
            case "getCoords":
                return gameUserService.getCoords(msgKey);
            case "getReplay":
                return gameUserService.getReplay(msgKey, params.get("id").asLong());
            case "setReplay":
                return gameUserService.setReplay(msgKey, params.get("id").asLong());
            case "shareWX":
                return gameUserService.shareWX(msgKey, params.path("game").asText());
            case "getPrepareRoom":
                return gameUserService.getPrepareRoom(msgKey);
            case "kickUser":
                return gameUserService.kickUser(msgKey,params, allParams);
            case "getRecordsByRoom":
                return gameUserService.getRecordsByRoom(msgKey,params.path("roomUid").asLong());
            case "getRoomInfo":
                return gameUserService.getRoomInfo(msgKey, params, allParams);
            case "guessCarUp2Agent":
                return gameUserService.guessCarUp2Agent(msgKey);
            case "guessCarBind":
                int referrerId = params.get("referrerId").asInt();
                return gameUserService.guessCarBindReferrer(msgKey,referrerId);

            case "bindInGame":
                int referrerId1 = params.get("referrerId").asInt();
                return gameUserService.bindInGame(msgKey,referrerId1);

            case "accessCode":
                String code = params.get("accessCode").asText();
                return gameUserService.accessCode(msgKey, code);

            case "getUserSimpleInfo":
                long userId = params.get("userId").asLong();
                return gameUserService.getUserSimpleInfo(msgKey, userId);
            case "authenticate":
                String name = params.path("name").asText();
                String idCard = params.path("idCard").asText();
                return gameUserService.authenticate(msgKey, name, idCard);
            case "getCoupon":
                int index = params.path("index").asInt();
                return gameUserService.getCoupon(msgKey, index);

            case "goodExchange":
                String userName = params.path("name").asText();
                String location = params.path("location").asText();
                String phone = params.path("phone").asText();
                int id = params.path("id").asInt();

                return gameUserService.goodExchange(msgKey, userName, location, id, phone);

            case "getChargeRecord":
                String recharge_source = params.path("type").asText("1");
                return gameUserService.getChargeRecord(msgKey, recharge_source);

            case "getChargeRecordGive":
                return gameUserService.getChargeRecordGive(msgKey);
            case "getDiscount":
                return gameUserService.getDiscount(msgKey);

            case "change2Money":
                return gameUserService.change2Money(msgKey);
            case "rebate2Gold":
                int num1 = params.path("num").asInt();
                return gameUserService.rebate2Gold(msgKey, num1);

            case "gold2Money":
                int num2 = params.path("num").asInt();
                return gameUserService.gold2Money(msgKey, num2);
            case "getRebateDetails":
                long uid = params.path("userId").asLong(msgKey.getUserId());
                return gameUserService.getRebateDetails(msgKey,uid);
            case "withdrawMoney":
                double num = params.path("num").asDouble();
                String userName1 = params.path("name").asText();
                String card = params.path("card").asText();
                String phone1 = params.path("phone").asText();
                String bankName = params.path("bankName").asText();
                return gameUserService.withdrawMoney(msgKey, num, userName1, card, phone1,bankName);
            case "getAllMember":
                return gameUserService.getAllMember(msgKey);
            case "setPlayerVip":
                long playerId = params.path("playerId").asLong();
                int vip = params.path("vip").asInt();
                return gameUserService.setPlayerVip(msgKey,playerId,2);
            case "getAllVip":
                return gameUserService.getAllVip(msgKey);
            case "getAllMail":
                return gameUserService.getAllMail(msgKey);
            case "readMail":
                long mailId = params.path("mailId").asLong();
                boolean readAll = params.path("readAll").asBoolean();
                return gameUserService.readMail(msgKey,mailId, readAll);
            case "getRank":
                int month = params.path("month").asInt();
                return gameUserService.getRank(msgKey, month);

            case "getRebateInfo":
                String date = params.path("date").asText("all");
                return gameUserService.getRebateInfo(msgKey, date);

            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
