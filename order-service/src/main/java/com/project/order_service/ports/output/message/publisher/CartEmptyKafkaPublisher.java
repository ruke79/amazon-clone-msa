package com.project.order_service.ports.output.message.publisher;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CartEmptyRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEmptyKafkaPublisher {

    private final KafkaTemplate<String, CartEmptyRequest> kafkaTemplate;

    public void publish(CartEmptyRequest message) {

        try {
            kafkaTemplate.send("cart-empty", message);

        } catch (Exception e) {

            log.error("Error while sending CartEmptyRequest" +
                    " to kafka with error: {}", e.getMessage());
        }
    }
}
