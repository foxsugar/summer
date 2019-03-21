package com.code.server.redis.config;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IConstant {
    long SECOND_5 = 1000L * 5;
    long HOUR_1 = 1000L * 60 * 60;
//    long HOUR_1 = 1000L * 60 * 3;

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
    //玩家gold
    String USER_GOLD = "user_gold";
    //玩家bean
    String USER_BEAN = "user_bean";

    String ROBOT_POOL = "robot_pool";

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

    //agent
    String AGENT_TOKEN = "agent_token:";
    String AGENT_REBATE = "agent_rebate";

    String AGENT_BEAN = "agent_bean";

    String SAVE_AGENT = "save_agent";

    String LOG_GAMENUM = "log_gameNum:";
    String LOG_GOLDINCOME = "log_goldIncome:";
    String LOG_CHARGE_REBATE = "log_chargeRebate";
    String LOG_OTHER_INFO = "log_otherInfo";

    String CLUB_MONEY = "club_money:";


}
