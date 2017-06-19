package com.code.server.redis.service;

import com.code.server.constant.exception.RegisterFailedException;
import com.code.server.redis.config.ServerInfo;
import com.code.server.redis.config.IConstant;
import com.code.server.redis.dao.IGateRedis;
import com.code.server.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public void register(String serverType,int gateId,String host,String domain, int port ) throws RegisterFailedException {
        BoundHashOperations<String,String,String> gateServer = redisTemplate.boundHashOps(GATE_SERVER_LIST);
        long lastHeart = getLastHeart(gateId);
        //存在game 加入
        if (lastHeart != 0) {
            long now = System.currentTimeMillis();
            //认为有相同的gateid 停止启动
            if (now - getLastHeart(gateId) < 6000) {
                throw new RegisterFailedException();
            }
            cleanGate(gateId);
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServerId(gateId);
        serverInfo.setServerType(serverType);
        serverInfo.setStartTime(LocalDateTime.now().toString());
        serverInfo.setDomain(domain);
        serverInfo.setHost(host);
        serverInfo.setPort(port);
        gateServer.put(String.valueOf(gateId), JsonUtil.toJson(serverInfo));

    }

    @Override
    public void heart(int gateId) {
        BoundHashOperations<String,String,String> heart_gate = redisTemplate.boundHashOps(HEART_GATE);
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
        //清除心跳
        redisTemplate.boundHashOps(HEART_GATE).delete(String.valueOf(gateId));

        //清除服务器列表
        redisTemplate.boundHashOps(GATE_SERVER_LIST).delete(String.valueOf(gateId));
    }

    @Override
    public long getLastHeart(int gateId) {
        BoundHashOperations<String,String,String> heart_gate = redisTemplate.boundHashOps(HEART_GATE);
        String time = heart_gate.get("" + gateId);
        if (time == null) {
            return 0;
        }
        return Long.parseLong(time);
    }

    @Override
    public Map<String,String>  getAllHeart(){
        BoundHashOperations<String,String,String> heart_gate = redisTemplate.boundHashOps(IConstant.HEART_GATE);
        return heart_gate.entries();
    }

    public List<ServerInfo> getAllServer(){
        BoundHashOperations<String,String,String> gate_server = redisTemplate.boundHashOps(IConstant.GATE_SERVER_LIST);
        List<ServerInfo> list = new ArrayList<>();
        gate_server.entries().values().forEach(str->list.add(JsonUtil.readValue(str,ServerInfo.class)));
        return list;
    }
}
