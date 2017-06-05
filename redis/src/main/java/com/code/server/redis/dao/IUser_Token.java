package com.code.server.redis.dao;

/**
 * Created by sunxianping on 2017/5/26.
 */
public interface IUser_Token {
    void setToken(long userId, String token);

    String getToken(long userId);

    void setTokenAccount(String account, String token);

    String getTokenAccount(String account);
}
