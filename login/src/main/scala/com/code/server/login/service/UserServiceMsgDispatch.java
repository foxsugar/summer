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

            case "getNickNamePlayer":

                return gameUserService.getNickNamePlayer(msgKey);
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
                return gameUserService.getChargeRecord(msgKey);

            case "getDiscount":
                return gameUserService.getDiscount(msgKey);

            case "change2Money":
                return gameUserService.change2Money(msgKey);

            case "withdrawMoney":
                double num = params.path("num").asDouble();
                String userName1 = params.path("name").asText();
                String card = params.path("card").asText();
                String phone1 = params.path("phone").asText();

                return gameUserService.withdrawMoney(msgKey, num, userName1, card, phone1);

            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
