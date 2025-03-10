package com.project.cart_service.config;


import com.google.common.collect.ImmutableMap;
import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.request.CartRollbackRequest;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.CouponUseRequest;

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
    public ConsumerFactory<String, String> paymentFailedConsumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "payment");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);        
        return new DefaultKafkaConsumerFactory<>(config);

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> paymentFailedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentFailedConsumerFactory());

        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CouponUseRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CouponUseRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(couponUseConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
    @Bean
    public ConsumerFactory<String, CouponUseRequest> couponUseConsumerFactory() {
        JsonDeserializer<CouponUseRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "coupon")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

   
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CartEmptyRequest> cartEmptyContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartEmptyRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartEmptyConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
    @Bean
    public ConsumerFactory<String, CartEmptyRequest> cartEmptyConsumerFactory() {
        JsonDeserializer<CartEmptyRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "cart")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, CartRollbackRequest> cartRollbackContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartRollbackRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartRollbackConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
    @Bean
    public ConsumerFactory<String, CartRollbackRequest> cartRollbackConsumerFactory() {
        JsonDeserializer<CartRollbackRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "cart-rollback")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                        .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed") 
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }
}
