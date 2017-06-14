package com.code.server.gate.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 *
 * @author 2017/3/24 14:36
 */
@Component
public class MsgConsumer {
//
//    private static final Logger logger = LoggerFactory.getLogger(MsgConsumer.class);
//
//    @KafkaListener(id = "gate", topicPartitions = {
//            @TopicPartition(topic = "gate_topic", partitions = "${serverConfig.gateId}")
//    })
//    public void listen1(ConsumerRecord<String, String> record) {
//
//        try {
//            System.out.println("消费 : " + record.toString());
//            System.out.println("分区 : " + record.partition());
//            System.out.println("gate 发送给玩家  指定partition");
//            KafkaMsgDispatch.send2Client(Long.parseLong(record.key()),record.value());
//        }catch (Exception e){
//            logger.error("gate_topic 消费异常 ",e);
//        }
//
////        ack.acknowledge();
//
//    }
//
//    @KafkaListener(id = "gate1", topicPartitions = {
//            @TopicPartition(topic = "gate_topic", partitions = {"1","2","3","4"})
//    })
//    public void listen3(ConsumerRecord<String, String> record) {
//
//        try {
//            System.out.println(record.toString());
//            System.out.println("分区 : " + record.partition());
//            System.out.println("gate 发送给玩家");
//            KafkaMsgDispatch.send2Client(Long.parseLong(record.key()),record.value());
//        }catch (Exception e){
//            logger.error("gate_topic 消费异常 ",e);
//        }
//
////        ack.acknowledge();
//
//    }
//
//
//    @KafkaListener(id = "inner", topicPartitions = {
//            @TopicPartition(topic = "inner_gate_topic", partitions = "${serverConfig.gateId}")
//    })
//    public void listen2(ConsumerRecord<?, ?> record,Acknowledgment ack) {
//
//        try {
//            System.out.println(record.toString());
//            System.out.println("处理完毕");
//        }catch (Exception e){
//            logger.error("inner_gate_topic 消费异常 ",e);
//        }
//
//        ack.acknowledge();
//
//
//
//
//    }


}