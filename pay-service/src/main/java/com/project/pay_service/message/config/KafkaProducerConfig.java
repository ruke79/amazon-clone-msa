package com.project.pay_service.message.config;

import com.google.common.collect.ImmutableMap;

import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.common.message.dto.response.PaymentResponse;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
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
                .put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
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
    }
   
    @Bean
    public KafkaTemplate<String, PaymentResponse> kafkaTemplate() {
        return new KafkaTemplate<>(paymentResponseProducerFactory());
    }

     
}
