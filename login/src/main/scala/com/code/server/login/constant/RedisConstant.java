package com.code.server.login.constant;

/**
 * Created by dajuejinxian on 2018/4/2.
 */
public interface RedisConstant {

    String TOKEN_PREFIX = "token_%s";

    Integer EXPIRE = 7200; //2小时
}
