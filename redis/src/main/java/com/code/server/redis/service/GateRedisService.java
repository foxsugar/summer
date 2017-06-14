package com.code.server.redis.service;

import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.redis.config.ServerInfo;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IGateRedis;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by sunxianping on 2017/6/13.
 */
@Service
public class GateRedisService implements IGateRedis,IConstant{

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void register(int gateId,String host, int port ) throws RegisterFailedException {
        BoundHashOperations<String,String,String> gateServer = redisTemplate.boundHashOps(IConstant.GATE_SERVER_LIST);
        String json = gateServer.get(""+gateId);
        ServerInfo gateServerInfo;
        //不存在此gate 加入
        if (json == null) {
            gateServerInfo = new ServerInfo(gateId, host, port);
        } else {
            gateServerInfo = JsonUtil.readValue(json, ServerInfo.class);
            long now = System.currentTimeMillis();
            //认为有相同的gateid 停止启动
            if (now - getLastHeart(gateId) < 6000) {
                throw new RegisterFailedException();
            }
            cleanGate(gateId);
        }

        gateServer.put(String.valueOf(gateId), JsonUtil.toJson(gateServerInfo));

    }

    @Override
    public void heart(int gateId) {
        BoundHashOperations<String,String,String> heart_gate = redisTemplate.boundHashOps(IConstant.HEART_GATE);
        heart_gate.put(""+gateId,""+System.currentTimeMillis());
    }

    @Override
    public void cleanGate(int gateId) {
        //删掉 user-gate
        BoundHashOperations<String,String,String> user_gate = redisTemplate.boundHashOps(USER_GATE);
        String gateStr = String.valueOf(gateId);
        if (user_gate != null) {
            List<String> removeUserList = new ArrayList<>();
            for(Map.Entry<String,String> entry : user_gate.entries().entrySet()){
                if (entry.getValue().equals(gateStr)) {
                    removeUserList.add(entry.getKey());
                }
            }
            if (removeUserList.size() > 0) {
                user_gate.delete(removeUserList.toArray());
            }

        }
    }

    @Override
    public long getLastHeart(int gateId) {
        BoundHashOperations<String,String,String> heart_gate = redisTemplate.boundHashOps(IConstant.HEART_GATE);
        String time = heart_gate.get("" + gateId);
        if (time == null) {
            return 0;
        }
        return Long.parseLong(time);
    }
}
