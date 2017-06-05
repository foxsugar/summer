package com.code.server.game.room;



import com.code.server.constant.kafka.IKafaTopic;
import com.code.server.constant.response.ResponseVo;
import com.code.server.kafka.MsgProducer;
import com.code.server.util.SpringUtil;

import java.util.List;

/**
 * Created by win7 on 2017/3/9.
 */
public class MsgSender {





    public static void sendMsg2Player(Object msg, long userId) {
        String gateId = RedisManager.getUserRedisService().getGateId(userId);
        if (gateId != null) {
            SpringUtil.getBean(MsgProducer.class).send2Partition(IKafaTopic.GATE_TOPIC,Integer.valueOf(gateId),""+userId,msg);
        }
    }

    public static void sendMsg2Player(Object msg, List<Long> users) {
        for (long id : users) {
            sendMsg2Player(msg,id);
        }
    }

    public static void sendMsg2Player(String service, String method, Object msg, List<Long> users) {
        sendMsg2Player(new ResponseVo(service,method,msg),users);
    }

    public static void sendMsg2Player(String service, String method, int code, List<Long> users) {
        sendMsg2Player(new ResponseVo(service,method,code),users);
    }

    public static void sendMsg2Player(String service, String method, Object msg, long userId) {
        sendMsg2Player(new ResponseVo(service,method,msg),userId);
    }

    public static void sendMsg2Player(String service, String method, int code, long userId) {
        sendMsg2Player(new ResponseVo(service,method,code),userId);
    }




}
