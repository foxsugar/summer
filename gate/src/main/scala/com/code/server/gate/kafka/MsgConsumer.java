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

    @KafkaListener(id = "gate", topicPartitions = {
            @TopicPartition(topic = "gate_topic", partitions = "${serverConfig.serverId}")
    })
    public void listen1(ConsumerRecord<Long, String> record) {

        System.out.println(record.toString());
        System.out.println("处理完毕");
        KafkaMsgDispatch.send2Client(record.key(),record.value());


    }


    @KafkaListener(id = "inner", topicPartitions = {
            @TopicPartition(topic = "inner_gate_topic", partitions = "${serverConfig.serverId}")
    })
    public void listen2(ConsumerRecord<?, ?> record) {

        System.out.println(record.toString());
        System.out.println("处理完毕");


    }


}