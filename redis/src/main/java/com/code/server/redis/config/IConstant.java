package com.code.server.redis.config;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IConstant {
    long SECOND_5 = 1000L * 5;
    long HOUR_1 = 1000L * 60 * 60;

    //房间-服务器(房间在哪个逻辑服务器)
    String ROOM_GAMESERVER = "room_gameServer";

    //玩家-房间 关系
    String USER_ROOM = "user_room";
    String USER_PREPAARE_ROOM = "user_prepare_room:";
    //房间 玩家
    String ROOM_USER = "room_user";

    //金币房间 玩家
    String GOLDROOM_USER = "goldRoom_user";

    //玩家-网关
    String USER_GATE = "user_gate";
    //玩家货币
    String USER_MONEY = "user_money";
    //玩家bean
    String USER_BEAN = "user_bean";

    //玩家token
    String USER_TOKEN = "user_token:";

    //用户名-user
    String ACCOUNT_USER = "account_user";
    //user-用户名
    String USER_ACCOUNT = "user_account";

    //openid-user
    String OPENID_USER = "openId_user";
    //user-openid
    String USER_OPENID = "user_openid";

    //游戏逻辑服务列表
    String GAME_SERVER_LIST = "game_server_list";

    //网关服务列表
    String GATE_SERVER_LIST = "gate_server_list";

    String HEART_GATE = "heart_gate";
    String HEART_GAME = "heart_game";

    String SAVE_USERS = "save_users";


}
