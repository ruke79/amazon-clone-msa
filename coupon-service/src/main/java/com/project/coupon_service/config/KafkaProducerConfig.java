package com.project.coupon_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.google.common.collect.ImmutableMap;

import com.project.common.message.dto.request.CouponUseRequest;

import java.util.Map;
@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.url}")
    private String kafkaServerUrl;

    @Bean
    public Map<String, Object> producerConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)
                //멱등성 프로듀서 명시적 설정
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .build();

    }


    @Bean
    public ProducerFactory<String, CouponUseRequest> couponUseProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CouponUseRequest> couponUseKafkaTemplate() {
        return new KafkaTemplate<>(couponUseProducerFactory());
    }
    
}
