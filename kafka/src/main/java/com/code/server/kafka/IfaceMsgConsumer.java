package com.code.server.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created by sunxianping on 2017/6/13.
 */
public interface IfaceMsgConsumer {
    void consumer(ConsumerRecord<String, String> record);
}
