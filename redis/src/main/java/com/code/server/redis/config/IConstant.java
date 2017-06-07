package com.code.server.redis.config;

/**
 * Created by sunxianping on 2017/5/25.
 */
public interface IConstant {

    //玩家-房间 关系
    String USERID_ROOMID = "userId_roomId";
    //房间-服务器(房间在哪个逻辑服务器)
    String ROOMID_SERVERID = "roomId_serverId";
    //玩家-网关
    String USERID_GATEID = "userId_gateId";
    //玩家货币
    String USER_MONEY = "user_money";
    //玩家bean
    String USER_BEAN = "user_bean";

    //玩家token
    String USER_TOKEN = "user_token";

    //用户名-user
    String ACCOUNT_USER = "account_user";
    //user-用户名
    String USER_ACCOUNT = "user_account";

    //openid-user
    String OPENID_USER = "openid_user";
    //user-openid
    String USER_OPENID = "user_openid";

    //游戏逻辑服务列表
    String GAME_SERVER_LIST = "game_server_list";

    //网关服务列表
    String GATE_SERVER_LIST = "gate_server_list";





}
