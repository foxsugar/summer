package com.code.server.game.room.kafka;

import com.code.server.constant.kafka.KafkaMsgKey;
import com.code.server.game.room.RoomMsgDispatch;
import com.code.server.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
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
    RoomMsgDispatch roomMsgDispatch;

    @KafkaListener(id = "room", topicPartitions = {
            @TopicPartition(topic = "roomService", partitions = "${serverConfig.serverId}")
    })
    public void listen(ConsumerRecord<String, String> record) {

        System.out.println(record.toString());
        System.out.println("处理完毕");

        String key = record.key();
        String value = record.value();
        KafkaMsgKey msgKey = JsonUtil.readValue(key,KafkaMsgKey.class);
        JsonNode msgValue = JsonUtil.readTree(value);

        roomMsgDispatch.dispatchMsg(msgKey,msgValue);


    }




}