package com.code.server.gate.kafka;

import com.code.server.kafka.IfaceMsgConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sunxianping on 2017/6/13.
 */
public class GateConsumer implements IfaceMsgConsumer {
    private static final Logger logger = LoggerFactory.getLogger(GateConsumer.class);

    @Override
    public void consumer(ConsumerRecord<String, String> record) {

        try {
            KafkaMsgDispatch.send2Client(Long.parseLong(record.key()), record.value());
        } catch (Exception e) {
            logger.error("gate_topic 消费异常 ", e);
        }
    }
}
