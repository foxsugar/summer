package com.code.server.game.mahjong.service;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.mahjong.kafka.MsgProducer;
import com.code.server.game.mahjong.response.ResponseVo;
import com.code.server.game.mahjong.util.ErrorCode;
import com.code.server.game.mahjong.util.SpringUtil;
import com.code.server.game.room.MsgSender;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.xml.internal.serializer.utils.MsgKey;
import net.sf.json.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created by sunxianping on 2017/5/23.
 */
public class MsgDispatch {

    public void dispatch(ConsumerRecord<String, String> record){

        String key = record.key();
        String value = record.value();

        KafkaMsgKey msgKey = JsonUtil.readValue(key,KafkaMsgKey.class);
        JsonNode jsonNode= JsonUtil.readTree(value);



        long userId = msgKey.getUserId();



        String service = jsonNode.get("service").asText();
        String method = jsonNode.get("method").asText();
        JsonNode params = jsonNode.get("params");


        int code = dispatchAllMsg(service, method, params);
        //客户端要的方法返回
        if (code != 0) {
            ResponseVo vo = new ResponseVo(service, method, code);
            MsgSender.sendMsg2Player(vo,userId);

        }

    }

    private static int dispatchAllMsg(String service, String method, JsonNode params) {
        switch (service) {

            case "GameLogicService":{
                return GameLogicService.dispatch(method, params);


            }

            default:
                return ErrorCode.REQUEST_PARAM_ERROR;
        }
    }




}
