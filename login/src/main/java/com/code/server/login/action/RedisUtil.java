package com.code.server.login.action;

import com.code.server.constant.db.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created by sunxianping on 2017/5/25.
 */
public class RedisUtil {
    @Autowired
    private StringRedisTemplate template;



    public static void setUserVo(UserInfo userInfo){

    }

}
