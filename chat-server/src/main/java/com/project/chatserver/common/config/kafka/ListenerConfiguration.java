package com.project.chatserver.common.config.kafka;

import com.project.chatserver.dto.notification.AlarmEventDto;
import com.project.chatserver.model.ChatMessage;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
public class ListenerConfiguration {
    @Value("${kafka.url}")
    private String kafkaServerUrl;
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ChatMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, ChatMessage> consumerFactory() {
        JsonDeserializer<ChatMessage> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "chat")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }
    // @Bean
    // ConcurrentKafkaListenerContainerFactory<String, String> kafkaDeletePostContainerFactory() {
    //     ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    //     factory.setConsumerFactory(deletePostConsumer());
    //     return factory;
    // }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AlarmEventDto> kafkaNotificationContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AlarmEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaNotificationConsumer());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, AlarmEventDto> kafkaNotificationConsumer() {
        JsonDeserializer<AlarmEventDto> deserializer = new JsonDeserializer<>(AlarmEventDto.class, false);
        deserializer.addTrustedPackages("*");
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "notification")
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

}
