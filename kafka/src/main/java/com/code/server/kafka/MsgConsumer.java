package com.code.server.kafka;

import com.code.server.util.ThreadPool;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by sunxianping on 2017/6/13.
 */
public class MsgConsumer {


    public static void startAConsumer(String topic, IfaceMsgConsumer msgConsumer) {

        ThreadPool.getInstance().executor.execute(() -> {

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConfig());
            //订阅topic
            consumer.subscribe(Arrays.asList(topic));
//            TopicPartition partition1 = new TopicPartition("producer_test", 1);
//            consumer.assign(Arrays.asList(partition0));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(200);

                for (ConsumerRecord<String, String> record : records) {
                    msgConsumer.consumer(record);
                    System.out.printf("offset = %d, key = %s, value = %s  \r\n", record.offset(), record.key(), record.value());
                }
//                consumer.commitAsync();

            }
        });
    }

    private static Properties getConfig() {
        Properties props = new Properties();
        //设置brokerServer(kafka)ip地址
        props.put("bootstrap.servers", "localhost:9092");
        //设置consumer group name
        props.put("group.id", "myGroup");
        //设置自动提交偏移量(offset),由auto.commit.interval.ms控制提交频率
        props.put("enable.auto.commit", "true");
        //偏移量(offset)提交频率
        props.put("auto.commit.interval.ms", "1000");
        //设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        //如果采用latest，消费者只能得道其启动后，生产者生产的消息
//            props.put("auto.offset.reset", "earliest");
        //
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    public static void startAConsumer(String topic, int partition, IfaceMsgConsumer msgConsumer) {
        ThreadPool.getInstance().executor.execute(() -> {
            TopicPartition partition0 = new TopicPartition(topic, partition);
//            TopicPartition partition1 = new TopicPartition("producer_test", 1);
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConfig());
            consumer.assign(Arrays.asList(partition0));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(200);
                for (ConsumerRecord<String, String> record : records) {
                    msgConsumer.consumer(record);
                    System.out.printf("offset = %d, key = %s, value = %s  \r\n", record.offset(), record.key(), record.value());
//                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
//                    long lastOffset = record.offset();
//                    consumer.commitSync(Collections.singletonMap(partition0, new OffsetAndMetadata(lastOffset + 1)));
                }

            }

        });
    }
}
