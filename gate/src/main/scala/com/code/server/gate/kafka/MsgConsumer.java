package com.code.server.gate.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @KafkaListener(id = "baz", topicPartitions = {
            @TopicPartition(topic = "gate", partitions = "${serverConfig.serverId}")
    })
    public void listen(ConsumerRecord<?, ?> record) {

        System.out.println(record.toString());
        System.out.println("处理完毕");


    }


}