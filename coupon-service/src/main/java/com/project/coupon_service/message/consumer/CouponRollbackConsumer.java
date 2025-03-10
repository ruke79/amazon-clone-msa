package com.project.coupon_service.message.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CouponRollbackRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRollbackConsumer {

    private final CouponRollbackMessageListener couponRollbackMessageListener;

    @KafkaListener(id="coupon", topics = "coupon-rollback", containerFactory = "couponRollbackContainerFactory")
    void receive(List<CouponRollbackRequest> messages) {

        messages.forEach(message->couponRollbackMessageListener.couponRollback(message));
    }

}
