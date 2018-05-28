package com.code.server.login.action;

/**
 * Created by dajuejinxian on 2018/5/15.
 */
public interface ErrorCode {
    int SUCCESS = 2000000;
    int ERROR = 3000000;

    int ALREADY_AGENT = 100;
    int NOT_WX_USER = 101;//未关注

    int NOT_LOGIN = 1000;//未关注


}
