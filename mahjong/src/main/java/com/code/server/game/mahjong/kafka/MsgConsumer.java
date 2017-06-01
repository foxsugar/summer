package com.code.server.game.mahjong.kafka;

import com.code.server.game.mahjong.util.SpringUtil;
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

    @KafkaListener(id = "gameLogicService", topicPartitions = {
            @TopicPartition(topic = "gameLogicService", partitions = "${serverConfig.serverId}")
    })
    public void listen(ConsumerRecord<?, ?> record) {


        System.out.println(record.toString());
        MsgProducer msgProducer = SpringUtil.getBean(MsgProducer.class);

        msgProducer.send("gate",0,record.toString());


    }


}