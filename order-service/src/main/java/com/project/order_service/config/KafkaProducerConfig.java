package com.project.order_service.config;

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
import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.request.CartRollbackRequest;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.message.dto.request.ProductUpdateRequest;

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
    public ProducerFactory<String, PaymentRequest> paymentRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }

    @Bean
    public KafkaTemplate<String, PaymentRequest> paymentRequestKafkaTemplate() {
        return new KafkaTemplate<>(paymentRequestProducerFactory());
    }

    @Bean
    public ProducerFactory<String, CouponRollbackRequest> couponRollbackProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CouponRollbackRequest> couponRollbackKafkaTemplate() {
        return new KafkaTemplate<>(couponRollbackProducerFactory());
    }

    @Bean
    public ProducerFactory<String, CartRollbackRequest> cartRollbackProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CartRollbackRequest> cartRollbackKafkaTemplate() {
        return new KafkaTemplate<>(cartRollbackProducerFactory());
    }

    @Bean
    public ProducerFactory<String, ProductUpdateRequest> productUpdateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }

    @Bean
    public KafkaTemplate<String, ProductUpdateRequest> productUpdateKafkaTemplate() {
        return new KafkaTemplate<>(productUpdateProducerFactory());
    }

    @Bean
    public ProducerFactory<String, CartEmptyRequest> cartEmptyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CartEmptyRequest> cartEmptyKafkaTemplate() {
        return new KafkaTemplate<>(cartEmptyProducerFactory());
    }
    
}
