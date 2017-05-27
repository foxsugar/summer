package com.code.server.login.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 * @author  2017/3/24 14:36
 */
@Component
public class MsgConsumer {
//    int count = 0;
//    @KafkaListener(topics = {"test","testkey"})
    @KafkaListener(id = "bar", topicPartitions =
            { @TopicPartition(topic = "test", partitions = { "0" })})
    public void processMessage(String content) {
//        count ++;
        System.out.println("开始接受消息");
        System.out.println(content);
        System.out.println("处理完毕");
        System.out.println("");
//        System.out.println(count);

    }




}