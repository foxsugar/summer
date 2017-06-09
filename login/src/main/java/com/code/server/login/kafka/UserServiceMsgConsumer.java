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
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 * @author  2017/3/24 14:36
 */
@Component
public class UserServiceMsgConsumer {



    @KafkaListener(id = "ggg", topicPattern  = "userService")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println(record);
        ThreadPool.getInstance().executor.execute(()->{
            String key = record.key();
            String value = record.value();
            KafkaMsgKey msgKey = JsonUtil.readValue(key, KafkaMsgKey.class);
            JsonNode msgValue = JsonUtil.readTree(value);
            UserServiceMsgDispatch userServiceMsgDispatch = SpringUtil.getBean(UserServiceMsgDispatch.class);
            userServiceMsgDispatch.dispatchMsg(msgKey,msgValue);
        });


    }




//
//    @KafkaListener(id = "userService", topicPartitions = {
//            @TopicPartition(topic = "userService", partitions = {"0","1","2","3"})
//    })
//    public void listen1(ConsumerRecord<String, String> record) {
//        System.out.println(record);
//        ThreadPool.getInstance().executor.execute(()->{
//            String key = record.key();
//            String value = record.value();
//            KafkaMsgKey msgKey = JsonUtil.readValue(key, KafkaMsgKey.class);
//            JsonNode msgValue = JsonUtil.readTree(value);
//            UserServiceMsgDispatch userServiceMsgDispatch = SpringUtil.getBean(UserServiceMsgDispatch.class);
//            userServiceMsgDispatch.dispatchMsg(msgKey,msgValue);
//        });
//
//
//        record.
//    }

}