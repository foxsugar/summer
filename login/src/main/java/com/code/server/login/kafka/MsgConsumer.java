package com.code.server.login.kafka;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.util.JsonUtil;
import com.code.server.util.SpringUtil;
import com.code.server.util.ThreadPool;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 * @author  2017/3/24 14:36
 */
@Component
public class MsgConsumer {





    @KafkaListener(id = "userservice", topicPartitions = {
            @TopicPartition(topic = "userService", partitions = "${serverConfig.serverId}")
    })
    public void listen2(ConsumerRecord<String, String> record) {
        ThreadPool.getInstance().executor.execute(()->{
            String key = record.key();
            String value = record.value();
            KafkaMsgKey msgKey = JsonUtil.readValue(key, KafkaMsgKey.class);
            JsonNode msgValue = JsonUtil.readTree(value);
            UserServiceMsgDispatch userServiceMsgDispatch = SpringUtil.getBean(UserServiceMsgDispatch.class);
            userServiceMsgDispatch.dispatchMsg(msgKey,msgValue);
        });


    }


}