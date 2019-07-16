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


    int KAFKA_MSG_ID_REFRESH_ROOM_INSTANCE = 3100;
    int KAFKA_MSG_ID_ROOM_CLUB_USER = 3101;

    int KAFKA_MSG_ID_ADD_REBATE = 3200;
    int KAFKA_MSG_ID_ADD_MONEY = 3201;
    int KAFKA_MSG_ID_ADD_REBATE_LONGCHENG = 3202;

    int KAFKA_MSG_ID_ADD_WIN_NUM = 3203;
    int KAFKA_MSG_ID_ADD_COUPON = 3204;




}
