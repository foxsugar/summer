package com.code.server.login.service;

import com.code.server.constant.game.Record;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.util.JsonUtil;

/**
 * Created by sunxianping on 2017/6/16.
 */
public class CenterMsgService implements IkafkaMsgId{

    public static void dispatch(KafkaMsgKey msgKey, String msg){
        int msgId = msgKey.getMsgId();
        switch (msgId) {
            case KAFKA_MSG_ID_GEN_RECORD:
                genRecord(msg);
                break;

        }
    }

    private static void genRecord(String msg){
        Record.RoomRecord roomRecord = JsonUtil.readValue(msg, Record.RoomRecord.class);
    }
}
