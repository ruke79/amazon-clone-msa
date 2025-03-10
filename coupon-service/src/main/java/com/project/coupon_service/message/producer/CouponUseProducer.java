package com.project.coupon_service.message.producer;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.project.common.message.dto.request.CouponUseRequest;




@Component
@RequiredArgsConstructor
public class CouponUseProducer {

    private final KafkaTemplate<String, CouponUseRequest> kafkaTemplate;

    @Transactional
    public void publish(CouponUseRequest request) {

        try {
            kafkaTemplate.send("coupon-use", request);
        } catch(KafkaException ex) {
            
        }
    }
}
