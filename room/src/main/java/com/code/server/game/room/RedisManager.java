package com.code.server.game.room;

import com.code.server.redis.service.RoomRedisService;
import com.code.server.redis.service.UserRedisService;
import com.code.server.util.SpringUtil;

/**
 * Created by sunxianping on 2017/6/1.
 */
public class RedisManager {
    private static RedisManager ourInstance = new RedisManager();

    public static RedisManager getInstance() {
        return ourInstance;
    }

    private RedisManager() {
    }

    public static UserRedisService getUserRedisService(){
        return SpringUtil.getBean(UserRedisService.class);
    }

    public static RoomRedisService getRoomRedisService(){
        return SpringUtil.getBean(RoomRedisService.class);
    }
}
