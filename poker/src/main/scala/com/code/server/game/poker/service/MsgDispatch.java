package com.code.server.game.poker.service;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.constant.response.ResponseVo;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sunxianping on 2017/5/23.
 */
public class MsgDispatch {

    private static final Logger logger = LoggerFactory.getLogger(MsgDispatch.class);

    public static void dispatch(ConsumerRecord<String, String> record) {
        try {
            String key = record.key();
            String value = record.value();

            KafkaMsgKey msgKey = JsonUtil.readValue(key, KafkaMsgKey.class);
            JsonNode jsonNode = JsonUtil.readTree(value);

            long userId = msgKey.getUserId();
            String roomId = msgKey.getRoomId();

            String service = jsonNode.get("service").asText();
            String method = jsonNode.get("method").asText();
            JsonNode params = jsonNode.get("params");

            int code = dispatchAllMsg(userId, roomId, service, method, params);
            //客户端要的方法返回
            if (code != 0) {
                ResponseVo vo = new ResponseVo(service, method, code);
                MsgSender.sendMsg2Player(vo, userId);

            }
        } catch (Exception e) {
            logger.error("poker 消息异常 ", e);
        }

    }

    private static int dispatchAllMsg(long userId, String roomId, String service, String method, JsonNode params) {
        switch (service) {

            case "gameService": {
                return GameService.dispatch(userId, method, roomId, params);
            }

            case "pokerRoomService": {
                return PokerRoomService.dispatch(userId, method, params);
            }

            case "reconnService": {
                return ReconnService.dispatch(userId, method, roomId);
            }

            default:
                return -1;
        }
    }


}
