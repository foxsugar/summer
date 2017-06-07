package com.code.server.redis.service;

import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IRoom_Server;
import com.code.server.redis.dao.IRoom_Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by sunxianping on 2017/5/26.
 */
@Service
public class RoomRedisService implements IRoom_Server ,IConstant,IRoom_Users{


    private static final String room_user = "room_user|";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getServerId(String roomId) {

        HashOperations<String,String,String> room_server = redisTemplate.opsForHash();
        return room_server.get(ROOMID_SERVERID, roomId);
    }

    @Override
    public void setServerId(String roomId, String serverid) {
        HashOperations<String,String,String> room_server = redisTemplate.opsForHash();
        room_server.put(ROOMID_SERVERID, roomId,serverid);

    }

    @Override
    public void removeServer(String roomId) {
        HashOperations<String,String,Integer> room_server = redisTemplate.opsForHash();
        room_server.delete(ROOMID_SERVERID, roomId);
    }

    @Override
    public boolean isExist(String roomId) {
        HashOperations<String,String,Integer> room_server = redisTemplate.opsForHash();
        return room_server.hasKey(ROOMID_SERVERID, roomId);
    }

    @Override
    public void addUser(String roomId, long userId) {
        SetOperations<String,Long> room_user = redisTemplate.opsForSet();
        room_user.add(getRoom_user_key(roomId), userId);
    }

    @Override
    public void removeUser(String roomId, long userId) {
        SetOperations<String,Long> room_user = redisTemplate.opsForSet();
        room_user.remove(roomId, userId);
    }

    @Override
    public void removeRoomUsers(String roomId) {
        SetOperations<String,Long> room_user = redisTemplate.opsForSet();
        room_user.getOperations().delete(getRoom_user_key(roomId));
    }

    @Override
    public Set<Long> getUsers(String roomId) {
        SetOperations<String,Long> room_user = redisTemplate.opsForSet();
        return room_user.members(roomId);
    }

    private String getRoom_user_key(String roomId){
        return room_user+roomId;
    }
}
