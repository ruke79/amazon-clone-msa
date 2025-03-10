package com.project.order_service.ports.output.message.publisher;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.message.dto.request.CouponRollbackRequest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRollbackKafkaPublisher {

    private final KafkaTemplate<String, CouponRollbackRequest> kafkaTemplate;

    @Transactional
    public void publish(CouponRollbackRequest message) {

        try {
            kafkaTemplate.send("coupon-rollback", message);

        } catch(KafkaException e) {

            log.error("Error while sending CouponRollbackRequest" +
            " to kafka with error: {}", e.getMessage());
        }
    }
}
