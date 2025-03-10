package com.project.coupon_service.config;


import com.google.common.collect.ImmutableMap;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.common.message.dto.response.PaymentResponse;

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

import java.util.HashMap;
import java.util.Map;
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka.url}")
    private String kafkaServerUrl;
   

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, UserCreatedRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserCreatedRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userCreatedConsumerFactory());
        factory.setBatchListener(false);
        return factory;
    }
    @Bean
    public ConsumerFactory<String, UserCreatedRequest> userCreatedConsumerFactory() {
        JsonDeserializer<UserCreatedRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "user-2")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

     @Bean
    ConcurrentKafkaListenerContainerFactory<String, CouponRollbackRequest> couponRollbackContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CouponRollbackRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(couponRollbackConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
    @Bean
    public ConsumerFactory<String, CouponRollbackRequest> couponRollbackConsumerFactory() {
        JsonDeserializer<CouponRollbackRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "coupon")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                        .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed") 
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, PaymentResponse> paymentResponseContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentResponseConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
    @Bean
    public ConsumerFactory<String, PaymentResponse> paymentResponseConsumerFactory() {
        JsonDeserializer<PaymentResponse> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "coupon-used")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                        .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed") 
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }
}
