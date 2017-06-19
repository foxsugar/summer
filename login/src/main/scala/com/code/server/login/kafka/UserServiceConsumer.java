package com.code.server.login.kafka;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.kafka.IfaceMsgConsumer;

import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created by sunxianping on 2017/6/13.
 */
public class UserServiceConsumer implements IfaceMsgConsumer {
    @Override
    public void consumer(ConsumerRecord<String, String> record) {
        ThreadPool.getInstance().executor.execute(() -> {
            String key = record.key();
            String value = record.value();
            KafkaMsgKey msgKey = JsonUtil.readValue(key, KafkaMsgKey.class);
            JsonNode msgValue = JsonUtil.readTree(value);
            UserServiceMsgDispatch userServiceMsgDispatch = SpringUtil.getBean(UserServiceMsgDispatch.class);
            userServiceMsgDispatch.dispatchMsg(msgKey, msgValue);
        });
    }
}
