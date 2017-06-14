package com.code.server.game.mahjong.kafka;


import com.code.server.game.mahjong.service.MsgDispatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
public class MahjongMsgConsumer {


//    @KafkaListener(id = "gameLogicService", topicPartitions = {
//            @TopicPartition(topic = "gameLogicService", partitions = "${serverConfig.serverId}")
//    })
//    public void listen(ConsumerRecord<String, String> record,Acknowledgment ack) {
//            MsgDispatch.dispatch(record);
//            ack.acknowledge();
//    }
//
//
//    @KafkaListener(id = "reconn_topic", topicPartitions = {
//            @TopicPartition(topic = "reconnService", partitions = "${serverConfig.serverId}")
//    })
//    public void listen_reconn(ConsumerRecord<String, String> record,Acknowledgment ack) {
//        MsgDispatch.dispatch(record);
//        ack.acknowledge();
//    }
//
//
//    @KafkaListener(id = "mjRoomService", topicPartitions = {
//            @TopicPartition(topic = "mahjongRoomService", partitions = "${serverConfig.serverId}")
//    })
//    public void listen_room(ConsumerRecord<String, String> record,Acknowledgment ack) {
//        MsgDispatch.dispatch(record);
//        ack.acknowledge();
//    }
//
//
//
//    @KafkaListener(id = "gate", topicPartitions = {
//            @TopicPartition(topic = "gate_topic", partitions = {"1","3","2","4"})
//    })
//    public void listen1(ConsumerRecord<String, String> record,Acknowledgment ack) {
//
//        try {
//            System.out.println(record.toString());
//            System.out.println("room里的gate 指定partition");
//        }catch (Exception e){
//        }
//
//        ack.acknowledge();
//
//    }

//    @KafkaListener(id = "gameLogicService", topics ="gate_topic")
//    public void listen(ConsumerRecord<String, String> record,Acknowledgment ack) {
//        MsgDispatch.dispatch(record);
//        ack.acknowledge();
//    }
//
//
//    @KafkaListener(id = "reconn_topic", topics ="reconnService")
//    public void listen_reconn(ConsumerRecord<String, String> record,Acknowledgment ack) {
//        MsgDispatch.dispatch(record);
//        ack.acknowledge();
//    }
//
//
//    @KafkaListener(id = "mahjongRoomService", topics ="mahjongRoomService")
//    public void listen_room(ConsumerRecord<String, String> record,Acknowledgment ack) {
//        MsgDispatch.dispatch(record);
//        ack.acknowledge();
//    }
}