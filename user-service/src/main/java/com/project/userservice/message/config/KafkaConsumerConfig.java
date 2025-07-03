package com.project.userservice.message.config;

import com.google.common.collect.ImmutableMap;
import com.project.common.dto.notification.AlarmEventDto;

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
    ConcurrentKafkaListenerContainerFactory<String, AlarmEventDto> kafkaNotificationContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AlarmEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaNotificationConsumer());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AlarmEventDto> kafkaNotificationConsumer() {
        JsonDeserializer<AlarmEventDto> deserializer = new JsonDeserializer<>(AlarmEventDto.class, false);
        deserializer.addTrustedPackages("*");
        Map<String, Object> consumerConfigurations = ImmutableMap.<String, Object>builder()
                .put(ConsumerConfig.GROUP_ID_CONFIG, "notification")
                .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
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
                .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
    }

}
