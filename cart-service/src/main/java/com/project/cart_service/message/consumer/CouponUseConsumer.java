package com.project.cart_service.message.consumer;


import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CouponUseRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponUseConsumer {

    private static int seq = 0;
    
    private final CouponUseMessageListener userMessageListener;
    
      @KafkaListener(id = "coupon", topics = "coupon-use") 
    public void received(List<CouponUseRequest> messages) {    

      //  messages.forEach(userCreated ->
      //    log.info("User Id : {}", userCreated));

         messages.forEach(couponUse -> 
         userMessageListener.couponUsed(couponUse) );
        
    }
}
