package com.project.pay_service.message.config;

import com.google.common.collect.ImmutableMap;

import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.common.message.dto.response.PaymentResponse;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.UUID;

@EnableKafka
@Configuration
public class KafkaProducerConfig {
    @Value("${kafka.url}")
    private String kafkaServerUrl;
    //Kafka ProduceFactory를 생성하는 Bean 메서드

    @Bean
    public ProducerFactory<String, PaymentResponse> paymentResponseProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }
    // Kafka Producer 구성을 위한 설정값들을 포함한 맵을 반환하는 메서드
    @Bean
    public Map<String, Object> producerConfig() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)                
                .put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString())
                .build();
    }
   
    @Bean
    public KafkaTemplate<String, PaymentResponse> kafkaTemplate() {
        return new KafkaTemplate<>(paymentResponseProducerFactory());
    }

     
}
