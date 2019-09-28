package com.code.server.game.room.kafka;


import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.response.ResponseVo;
import com.code.server.kafka.MsgProducer;
import com.code.server.redis.service.RedisManager;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win7 on 2017/3/9.
 */
public class MsgSender {
    public static void sendMsg2Player(Object msg, long userId) {
        String gateId = RedisManager.getUserRedisService().getGateId(userId);
        if (gateId != null) {
            SpringUtil.getBean(MsgProducer.class).send2Partition(IKafaTopic.GATE_TOPIC, Integer.valueOf(gateId), "" + userId, msg);
        }
    }

    public static void sendMsg2Player(Object msg, List<Long> users) {
        List<Long> temp = new ArrayList<>(users);
        for (long id : temp) {
            sendMsg2Player(msg, id);
        }
    }

    public static void sendMsg2Player(String service, String method, Object msg, List<Long> users) {
        sendMsg2Player(new ResponseVo(service, method, msg), users);
    }

    public static void sendMsg2Player(String service, String method, int code, List<Long> users) {
        sendMsg2Player(new ResponseVo(service, method, code), users);
    }

    public static void sendMsg2Player(String service, String method, Object msg, long userId) {
        sendMsg2Player(new ResponseVo(service, method, msg), userId);
    }

    public static void sendMsg2Player(String service, String method, int code, long userId) {
        sendMsg2Player(new ResponseVo(service, method, code), userId);
    }

    //增加发完消息之后的callback
    public static void sendMsg2Player(String service, String method, Object msg, List<Long> users, IfaceMsgSender iface){
        sendMsg2Player(service, method, msg, users);
        if (iface != null){

            Object obj = new ResponseVo(service, method, msg);
            String json = JsonUtil.toJson(obj);
            for (Long uid : users){
                iface.callback(json, uid);
            }
        }
    }
    //增加发完消息之后的callback
    public static void sendMsg2Player(String service, String method, int code, List<Long> users, IfaceMsgSender iface) {
        sendMsg2Player(service, method, code, users);
        if (iface != null){
            Object obj = new ResponseVo(service, method, code);
            String json = JsonUtil.toJson(obj);
            for (Long uid : users){
                iface.callback(json, uid);
            }
        }
    }

    public static void sendMsg2Player(String service, String method, Object msg, long userId, IfaceMsgSender iface){
        sendMsg2Player(service, method, msg, userId);

        if (iface != null){
            Object obj = new ResponseVo(service, method, msg);
            String json = JsonUtil.toJson(obj);
            iface.callback(json, userId);
        }
    }

    public static void sendMsg2Player(String service, String method, int code, long userId, IfaceMsgSender iface) {
        sendMsg2Player(new ResponseVo(service, method, code), userId);

        if (iface != null){
            Object obj = new ResponseVo(service, method, code);
            String json = JsonUtil.toJson(obj);
            iface.callback(json, userId);
        }
    }

    public static void sendMsg2Player(Object msg, long userId, IfaceMsgSender iface) {
        sendMsg2Player(msg, userId);
        if (iface != null){
            Object obj = msg;
            String json = JsonUtil.toJson(obj);
            iface.callback(json, userId);
        }
    }

    public static void sendMsg2Player(Object msg, List<Long> users, IfaceMsgSender iface){
        sendMsg2Player(msg, users);
        if (iface != null){
            Object obj = msg;
            String json = JsonUtil.toJson(obj);
            for (Long uid : users){
                iface.callback(json, uid);
            }
        }
    }



}
