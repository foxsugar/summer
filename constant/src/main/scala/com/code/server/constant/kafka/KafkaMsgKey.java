package com.code.server.constant.kafka;

/**
 * Created by sunxianping on 2017/6/7.
 */
public class KafkaMsgKey {
    public int msgId;
    public long userId;
    public int partition;
    public String roomId;

    public int getMsgId() {
        return msgId;
    }

    public KafkaMsgKey setMsgId(int msgId) {
        this.msgId = msgId;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public KafkaMsgKey setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public int getPartition() {
        return partition;
    }

    public KafkaMsgKey setPartition(int partition) {
        this.partition = partition;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public KafkaMsgKey setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }
}
