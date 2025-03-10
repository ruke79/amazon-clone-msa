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
import org.springframework.orm.jpa.JpaTransactionManager;

import com.google.common.collect.ImmutableMap;
import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.request.CartRollbackRequest;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.message.dto.request.ProductUpdateRequest;

import jakarta.persistence.EntityManagerFactory;

import java.util.Map;
import java.util.UUID;
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
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString())
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
    public Map<String, Object> couponRollbackConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)                
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString())
                .build();
    }


    @Bean
    public ProducerFactory<String, CouponRollbackRequest> couponRollbackProducerFactory() {
        return new DefaultKafkaProducerFactory<>(couponRollbackConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CouponRollbackRequest> couponRollbackKafkaTemplate() {
        return new KafkaTemplate<>(couponRollbackProducerFactory());
    }

    @Bean
    public Map<String, Object> cartRollbackConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)                
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public ProducerFactory<String, CartRollbackRequest> cartRollbackProducerFactory() {
        return new DefaultKafkaProducerFactory<>(cartRollbackConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CartRollbackRequest> cartRollbackKafkaTemplate() {
        return new KafkaTemplate<>(cartRollbackProducerFactory());
    }

    @Bean
    public Map<String, Object> ProductUpdateConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)                
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString())
                .build();

    }

    @Bean
    public ProducerFactory<String, ProductUpdateRequest> productUpdateProducerFactory() {
        return new DefaultKafkaProducerFactory<>(ProductUpdateConfigurations());

    }

    @Bean
    public KafkaTemplate<String, ProductUpdateRequest> productUpdateKafkaTemplate() {
        return new KafkaTemplate<>(productUpdateProducerFactory());
    }

    @Bean
    public Map<String, Object> cartEmptyConfigurations() {
        return ImmutableMap.<String, Object>builder()
                .put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)                
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
                .put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString())
                .build();

    }

    @Bean
    public ProducerFactory<String, CartEmptyRequest> cartEmptyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(cartEmptyConfigurations());

    }

    @Bean
    public KafkaTemplate<String, CartEmptyRequest> cartEmptyKafkaTemplate() {
        return new KafkaTemplate<>(cartEmptyProducerFactory());
    }
    
}
