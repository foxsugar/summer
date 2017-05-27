package com.code.server.center.kafka;

import com.code.server.db.Service.UserService;
import com.code.server.db.model.User;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 *
 * @author 2017/3/24 14:36
 */
@Component
public class MsgConsumer {

    @Autowired
    public UserService userService;

    @KafkaListener(id = "baz", topicPartitions = {
            @TopicPartition(topic = "center", partitions = "${serverConfig.serverId}")
    })
    public void listen(ConsumerRecord<String, String> record) {
        Gson gson = new Gson();
        String result = record.value();
        Object o = gson.fromJson(result, Object.class);
        if(o instanceof User){
            userService.save((User)o);
        }
    }


}