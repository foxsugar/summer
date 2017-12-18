package com.code.server.constant.kafka;

/**
 * Created by sunxianping on 2017/6/7.
 */
public interface IkafkaMsgId {
    int KAFKA_MSG_ID_GEN_RECORD = 1000;
    int KAFKA_MSG_ID_REPLAY = 1001;
    int KAFKA_MSG_ID_GAME_RECORD = 1002;
    int KAFKA_MSG_ID_ROOM_RECORD = 1003;

    int KAFKA_MSG_ID_GATE_KICK_USER = 2000;

    int KAFKA_MSG_ID_GUESS_ADD_GOLD = 3000;

}
