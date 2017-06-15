package com.code.server.game.room.kafka;

import com.code.server.game.room.RoomMsgDispatch;
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
public class RoomMsgConsumer {

    @Autowired
    RoomMsgDispatch roomMsgDispatch;

    @KafkaListener(id = "room", topicPartitions = {
            @TopicPartition(topic = "roomService", partitions = "${serverConfig.serverId}")
    })
    public void listen(ConsumerRecord<String, String> record) {
        RoomMsgDispatch.dispatch(record);
    }




}