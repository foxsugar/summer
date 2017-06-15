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


    @KafkaListener(id = "gameLogicService", topicPartitions = {
            @TopicPartition(topic = "gameLogicService", partitions = "${serverConfig.serverId}")
    })
    public void listen(ConsumerRecord<String, String> record ) {
            MsgDispatch.dispatch(record);
    }


    @KafkaListener(id = "reconnService", topicPartitions = {
            @TopicPartition(topic = "reconnService", partitions = "${serverConfig.serverId}")
    })
    public void listen_reconn(ConsumerRecord<String, String> record ) {
        MsgDispatch.dispatch(record);
    }


    @KafkaListener(id = "mahjongRoomService", topicPartitions = {
            @TopicPartition(topic = "mahjongRoomService", partitions = "${serverConfig.serverId}")
    })
    public void listen_room(ConsumerRecord<String, String> record ) {
        MsgDispatch.dispatch(record);
    }

}