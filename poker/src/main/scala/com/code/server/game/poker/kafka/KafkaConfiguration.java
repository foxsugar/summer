package com.code.server.game.poker.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Created by sunxianping on 2017/5/4.
 */
@Configuration
@EnableKafka()
public class KafkaConfiguration {

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