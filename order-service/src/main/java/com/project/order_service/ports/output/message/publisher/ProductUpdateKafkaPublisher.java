package com.project.order_service.ports.output.message.publisher;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.ProductUpdateRequest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductUpdateKafkaPublisher {

    private final KafkaTemplate<String, ProductUpdateRequest> kafkaTemplate;

    public void publish(ProductUpdateRequest message) {

        try {
            kafkaTemplate.send("product-update", message);

        } catch(Exception e) {

            log.error("Error while sending ProductUpdateRequest" +
            " to kafka with error: {}", e.getMessage());
        }
    }
}
