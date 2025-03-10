package com.project.pay_service.message.config;


import com.google.common.collect.ImmutableMap;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.message.dto.request.UserCreatedRequest;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.adapter.DefaultBatchToRecordAdapter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${kafka.url}")
    private String kafkaServerUrl;

    ConsumerRecord<?, ?> failed;
      
    @Bean
    public ConsumerFactory<String, PaymentRequest> paymentRequestConsumerFactory() {
        JsonDeserializer<PaymentRequest> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");
        
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, "payment-request")
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
                        .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed")
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
                        .build();

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentRequestConsumerFactory());
        factory.setBatchListener(true);
        factory.setBatchToRecordAdapter(new DefaultBatchToRecordAdapter<>((record, ex) ->  {
            this.failed = record;
        }));

        return factory;

    }
}
