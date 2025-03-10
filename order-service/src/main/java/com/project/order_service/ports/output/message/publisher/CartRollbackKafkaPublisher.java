package com.project.order_service.ports.output.message.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CartRollbackRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartRollbackKafkaPublisher {

    private final KafkaTemplate<String, CartRollbackRequest> kafkaTemplate;

    public void publish(CartRollbackRequest message) {

        try {
            kafkaTemplate.send("cart-rollback", message);

        } catch (Exception e) {

            log.error("Error while sending CartRollbackRequest" +
                    " to kafka with error: {}", e.getMessage());
        }
    }
}
