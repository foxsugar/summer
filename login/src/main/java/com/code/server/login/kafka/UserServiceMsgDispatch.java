package com.code.server.login.kafka;

import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ErrorCode;
import com.code.server.constant.response.ResponseVo;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import scala.tools.nsc.doc.html.page.JSONObject;

/**
 * Created by sunxianping on 2017/5/31.
 */
public class UserServiceMsgDispatch {

    @Autowired
    GameUserService gameUserService;

    @Autowired
    MsgProducer kafkaMsgProducer;


    public void dispatchMsg(KafkaMsgKey msgKey, JsonNode params) {
        String service = params.get("service").asText();
        String method = params.get("method").asText();
        JsonNode node = params.get("params");
        int rtn = dispatchUserService(msgKey, node);
        if (rtn != 0) {
            ResponseVo vo = new ResponseVo(service, method, rtn);
            kafkaMsgProducer.send2Partition(IKafaTopic.GATE_TOPIC, msgKey.getPartition(),""+msgKey.getUserId(),vo);
        }
    }

    private int dispatchUserService(KafkaMsgKey msgKey, JsonNode params) {

        String method = params.get("method").asText();
        switch (method) {

            case "getUserMessage": {

                return gameUserService.getUserMessage(msgKey);
            }

            case "getUserRecodeByUserId": {

                Player player = GameManager.getPlayerByCtx(ctx);
                if (player == null) {
                    return ErrorCode.YOU_HAVE_NOT_LOGIN;
                }
                int type = params.getInt("type");
                return gameUserService.getUserRecodeByUserId(player, type);

            }
            case "bindReferrer": {
                Player player = GameManager.getPlayerByCtx(ctx);
                if (player == null) {
                    return ErrorCode.YOU_HAVE_NOT_LOGIN;
                }
                int referrerId = params.getInt("referrerId");
                return gameUserService.bindReferrer(player, referrerId);
            }


            case "giveOtherMoney":
                Player player = GameManager.getPlayerByCtx(ctx);
                Long accepterId = Long.parseLong(params.getString("accepterId"));
                int money = Integer.parseInt(params.getString("money"));
                return gameUserService.giveOtherMoney(player, accepterId, money);

            case "getNickNamePlayer":
                Player giver = GameManager.getPlayerByCtx(ctx);
                Long accepterUserId = Long.parseLong(params.getString("accepterId"));
                return gameUserService.getNickNamePlayer(giver, accepterUserId);

            default:

                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }
}
