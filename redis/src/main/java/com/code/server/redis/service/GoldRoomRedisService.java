package com.code.server.redis.service;

import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IRoom_Server;
import com.code.server.redis.dao.IRoom_Users;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Clark on 2017/8/7.
 * 金币场房间
 */
@Service
public class GoldRoomRedisService implements IRoom_Server,IConstant,IRoom_Users {

    private static final String goldRoom_user = "goldRoom_user|";

    @Autowired
    private RedisTemplate redisTemplate;


    public void addUserOfGold(long userId) {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(GOLDROOM_USER);
        Set<Long> users ;
        String json = room_user.get("000000");
        if (json == null) {
            users = new HashSet<>();
        } else {
            users = JsonUtil.readValue(json, new TypeReference<Set<Long>>() {});
        }
        users.add(userId);
        room_user.put("000000",JsonUtil.toJson(users));
    }


    public void removeUserOfGold(long userId) {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(GOLDROOM_USER);
        String json = room_user.get("000000");
        if (json != null) {
            Set<Long> users  = JsonUtil.readValue(json, new TypeReference<Set<Long>>() {});
            users.remove(userId);
            room_user.put("000000",JsonUtil.toJson(users));
        }
    }


    public void removeRoomUsersOfGold() {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(GOLDROOM_USER);
        room_user.delete("000000");
    }


    public Set<Long> getUsersOfGold() {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(GOLDROOM_USER);
        Set<Long> users ;
        String json = room_user.get("000000");
        if (json == null) {
            users = new HashSet<>();
        } else {
            users = JsonUtil.readValue(json, new TypeReference<Set<Long>>() {});
        }
        return users;
    }

    @Override
    public String getServerId(String roomId) {

        HashOperations<String,String,String> room_server = redisTemplate.opsForHash();
        return room_server.get(ROOM_GAMESERVER, roomId);
    }

    @Override
    public void setServerId(String roomId, String serverid) {
        HashOperations<String,String,String> room_server = redisTemplate.opsForHash();
        room_server.put(ROOM_GAMESERVER, roomId,serverid);

    }

    @Override
    public void removeServer(Object... roomId) {
        HashOperations<String,String,String> room_server = redisTemplate.opsForHash();
        room_server.delete(ROOM_GAMESERVER, roomId);
    }

    @Override
    public boolean isExist(String roomId) {
        HashOperations<String,String,String> room_server = redisTemplate.opsForHash();
        return room_server.hasKey(ROOM_GAMESERVER, roomId);
    }

    @Override
    public void addUser(String roomId, long userId) {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(ROOM_USER);
        Set<Long> users ;
        String json = room_user.get(roomId);
        if (json == null) {
            users = new HashSet<>();
        } else {
            users = JsonUtil.readValue(json, new TypeReference<Set<Long>>() {});
        }
        users.add(userId);
        room_user.put(roomId,JsonUtil.toJson(users));
    }

    @Override
    public void removeUser(String roomId, long userId) {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(ROOM_USER);
        String json = room_user.get(roomId);
        if (json != null) {
            Set<Long> users  = JsonUtil.readValue(json, new TypeReference<Set<Long>>() {});
            users.remove(userId);
            room_user.put(roomId,JsonUtil.toJson(users));
        }
    }

    @Override
    public void removeRoomUsers(Object... roomId) {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(ROOM_USER);
        room_user.delete( roomId);
    }

    @Override
    public Set<Long> getUsers(String roomId) {
        BoundHashOperations<String,String,String> room_user = redisTemplate.boundHashOps(ROOM_USER);
        Set<Long> users ;
        String json = room_user.get(roomId);
        if (json == null) {
            users = new HashSet<>();
        } else {
            users = JsonUtil.readValue(json, new TypeReference<Set<Long>>() {});
        }
        return users;
    }

    private String getRoom_user_key(String roomId){
        return goldRoom_user + roomId;
    }

    @Override
    public int getRoomNum() {
        return 0;
    }
}
