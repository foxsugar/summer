package com.code.server.login.service;

import com.code.server.constant.game.Record;
import com.code.server.constant.kafka.IkafkaMsgId;
import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.db.Service.ReplayService;
import com.code.server.db.Service.UserRecordService;
import com.code.server.db.model.Replay;
import com.code.server.db.model.UserRecord;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;

import java.util.List;

/**
 * Created by sunxianping on 2017/6/16.
 */
public class CenterMsgService implements IkafkaMsgId{


    private static UserRecordService userRecordService = SpringUtil.getBean(UserRecordService.class);

    private static ReplayService replayService = SpringUtil.getBean(ReplayService.class);

    public static void dispatch(KafkaMsgKey msgKey, String msg){
        int msgId = msgKey.getMsgId();
        switch (msgId) {
            case KAFKA_MSG_ID_GEN_RECORD:
                genRecord(msg);
                break;
            case KAFKA_MSG_ID_REPLAY:
                replay(msg);


        }
    }

    private static void genRecord(String msg){
        Record.RoomRecord roomRecord = JsonUtil.readValue(msg, Record.RoomRecord.class);

        List<Record.UserRecord> lists = roomRecord.getRecords();
        for(Record.UserRecord userRecord: lists){
            UserRecord addRecord = userRecordService.getUserRecordByUserId(userRecord.getUserId());
                if (addRecord!=null){
                    userRecordService.addRecord(userRecord.getUserId(),roomRecord);
                }else{
                    Record record = new Record();
                    record.addRoomRecord(roomRecord);

                    UserRecord newRecord = new UserRecord();
                    newRecord.setId(userRecord.getUserId());
                    newRecord.setRecord(record);
                    userRecordService.save(newRecord);
                }
        }
    }

    private static void replay(String msg){
        if (msg != null) {
            long id = JsonUtil.readTree(msg).path("id").asLong();
            int count = JsonUtil.readTree(msg).path("count").asInt();
            Replay replay = new Replay();
            replay.setId(id);
            replay.setLeftCount(count);
            replay.setData(msg);
            replayService.save(replay);
        }

    }
}
