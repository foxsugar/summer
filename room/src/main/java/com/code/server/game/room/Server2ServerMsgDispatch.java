package com.code.server.game.room;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.room.kafka.MsgSender;
import com.code.server.game.room.service.RoomManager;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * Created by sunxianping on 2017/10/31.
 */
public class Server2ServerMsgDispatch {

    public static void dispatch(ConsumerRecord<String,String> record){
        KafkaMsgKey msgKey = JsonUtil.readValue(record.key(), KafkaMsgKey.class);

        JsonNode params = JsonUtil.readTree(record.value());

        String service = params.get("service").asText();
        String method = params.get("method").asText();
        JsonNode node = params.get("params");
        switch (method) {
            case "kickUser":
                kickUser(msgKey, node.path("roomId").asText(), node.path("kickUser").asLong());
                break;
        }
    }

    public static void kickUser(KafkaMsgKey kafkaMsgKey, String roomId,long kickUser){
        IfaceRoom room = RoomManager.getRoom(roomId);
        long userId = kafkaMsgKey.getUserId();
        int rtn = room.quitRoom(kickUser);
        List<Long> users = room.getUsers();
        if (rtn != 0) {
            MsgSender.sendMsg2Player("userService","kickUser", users, kafkaMsgKey.getUserId());
        }

    }
}
