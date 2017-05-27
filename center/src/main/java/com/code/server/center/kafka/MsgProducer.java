package com.code.server.center.kafka;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 * @author  2017/3/24 14:36
 */
@Component
public class MsgProducer {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void send(String topic, int partition, String data) {
        kafkaTemplate.send(topic, partition, data);
    }

    public void send(String topic, int partition, String key, String data) {
        kafkaTemplate.send(topic, partition, key,data);
    }

    public void send(String topic, String data) {
        kafkaTemplate.send(topic, data);
    }
    public void send() {
        kafkaTemplate.send("test", 3, "111111111");
        kafkaTemplate.metrics();
        kafkaTemplate.execute(new KafkaOperations.ProducerCallback<String, String, Object>() {
            @Override
            public Object doInKafka(Producer<String, String> producer) {
                //这里可以编写kafka原生的api操作
                return null;
            }
        });

        //消息发送的监听器，用于回调返回信息
        kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
            @Override
            public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata recordMetadata) {
                System.out.println("success");
            }

            @Override
            public void onError(String topic, Integer partition, String key, String value, Exception exception) {
                exception.printStackTrace();
                System.out.print("error");
            }

            @Override
            public boolean isInterestedInSuccess() {
                return false;
            }
        });
    }
}