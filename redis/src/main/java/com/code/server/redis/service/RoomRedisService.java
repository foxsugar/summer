package com.code.server.redis.service;

import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IRoom_Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by sunxianping on 2017/5/26.
 */
@Service
public class RoomRedisService implements IRoom_Server ,IConstant{


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public int getServerId(String roomId) {

        HashOperations<String,String,Integer> user_room = redisTemplate.opsForHash();
        return user_room.get(ROOMID_SERVERID, roomId);
    }

    @Override
    public void setServerId(String roomId, int serverid) {
        HashOperations<String,String,Integer> user_room = redisTemplate.opsForHash();
        user_room.put(ROOMID_SERVERID, roomId,serverid);

    }

    @Override
    public void removeServer(String roomId) {
        HashOperations<String,String,Integer> user_room = redisTemplate.opsForHash();
        user_room.delete(ROOMID_SERVERID, roomId);
    }
}
