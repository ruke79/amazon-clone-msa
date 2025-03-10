package com.project.coupon_service.message.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CouponUseRequest;




@Component
@RequiredArgsConstructor
public class CouponUseProducer {

    private final KafkaTemplate<String, CouponUseRequest> kafkaTemplate;


    public void publish(CouponUseRequest request) {
        kafkaTemplate.send("coupon-use", request);
    }
}
