package com.code.server.constant.kafka

/**
 * Created by sunxianping on 2017/5/31.
 */
class KafkaMsgKey {
    static final int MSG_TYPE_GATE2GATE = 1
    int msg_type
    long userId
    int partition
    String roomId
}
