package com.project.cart_service.config;

import com.google.common.collect.ImmutableMap;
import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.request.CartRollbackRequest;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.CouponUseRequest;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
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
    @Value("${kafka.security.protocol}")
    private String securityProtocol;

    @Value("${kafka.ssl.trust-store-password}")
    private String trustStorePassword;

    @Value("${kafka.ssl.trust-store-location}")
    private String trustStoreLocation;

    @Value("${kafka.ssl.key-store-location}")
    private String keyStoreLocation;

    @Value("${kafka.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${kafka.url}")
    private String kafkaServerUrl;

    String checkPath(String keystorePath) {
        if (keystorePath.startsWith("file://"))
            return keystorePath.replaceFirst("file://", "");
        else if (keystorePath.startsWith("file:"))
            return keystorePath.replaceFirst("file:", "");
        return keystorePath;
    }

    @Bean
    public ConsumerFactory<String, String> paymentFailedConsumerFactory() {
        JsonDeserializer<String> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");

        Map<String, Object> consumerConfigurations = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "payment")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed")
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                .put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                        checkPath(keyStoreLocation))
                .put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                        keyStorePassword)
                .put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG,
                        "JKS")
                .put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                        checkPath(trustStoreLocation))
                .put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                        trustStorePassword)
                .put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG,
                        "JKS")
                .put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG,
                        securityProtocol)
                // .put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                // "org.apache.kafka.clients.consumer.CooperativeStickyAssignor")
                .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);

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

        Map<String, Object> consumerConfigurations = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "coupon")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed")
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                .put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                        checkPath(keyStoreLocation))
                .put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                        keyStorePassword)
                .put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG,
                        "JKS")
                .put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                        checkPath(trustStoreLocation))
                .put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                        trustStorePassword)
                .put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG,
                        "JKS")
                .put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG,
                        securityProtocol)
                // .put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                // "org.apache.kafka.clients.consumer.CooperativeStickyAssignor")
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

        Map<String, Object> consumerConfigurations = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "cart")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                 .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed")
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                .put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                        checkPath(keyStoreLocation))
                .put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                        keyStorePassword)
                .put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG,
                        "JKS")
                .put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                        checkPath(trustStoreLocation))
                .put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                        trustStorePassword)
                .put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG,
                        "JKS")
                .put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG,
                        securityProtocol)
                // .put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                // "org.apache.kafka.clients.consumer.CooperativeStickyAssignor")
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

        Map<String, Object> consumerConfigurations = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                .put(ConsumerConfig.GROUP_ID_CONFIG, "cart-rollback")
                .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                 .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed")
                .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                .put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                        checkPath(keyStoreLocation))
                .put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                        keyStorePassword)
                .put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG,
                        "JKS")
                .put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                        checkPath(trustStoreLocation))
                .put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,
                        trustStorePassword)
                .put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG,
                        "JKS")
                .put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG,
                        securityProtocol)
                // .put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                // "org.apache.kafka.clients.consumer.CooperativeStickyAssignor")
                .build();


        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }
}
