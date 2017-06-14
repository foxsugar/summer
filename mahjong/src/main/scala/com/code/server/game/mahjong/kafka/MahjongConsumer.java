package com.code.server.game.mahjong.kafka;

import com.code.server.kafka.IfaceMsgConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created by sunxianping on 2017/6/13.
 */
public class MahjongConsumer implements IfaceMsgConsumer {
    @Override
    public void consumer(ConsumerRecord<String, String> record) {

    }
}
