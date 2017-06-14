package com.code.server.game.mahjong.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

/**
 * Created by sunxianping on 2017/5/4.
 */
@Configuration
@EnableKafka()
public class KafkaConfiguration {
//
//    @Bean
//    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
//    kafkaManualAckListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(manualConsumerFactory());
//        ContainerProperties props = factory.getContainerProperties();
//        props.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
//        props.setIdleEventInterval(100L);
//        factory.setRecordFilterStrategy(manualFilter());
//        factory.setAckDiscarded(true);
//        factory.setRetryTemplate(new RetryTemplate());
//        factory.setRecoveryCallback(c -> null);
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<Integer, String> manualConsumerFactory() {
//        Map<String, Object> configs = consumerConfigs();
//        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        return new DefaultKafkaConsumerFactory<>(configs);
//    }
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testAnnot", "false", embeddedKafka);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        return consumerProps;
//    }
//    @Bean
//    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
//    kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setConcurrency(3);
//        factory.getContainerProperties().setPollTimeout(3000);
//
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<Integer, String> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
//    }
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ProducerConfig.TIMEOUT_CONFIG,1000000);
//
//        return props;
//    }


}