package com.project.coupon_service.message.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.response.PaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseConsumer {

    private final PaymentResponseMessageListener paymentResponseMessageListener;

    @KafkaListener(id="coupon-used", topics = "payment", containerFactory = "paymentResponseContainerFactory")
    public void receive(List<PaymentResponse> messages) {
        
        messages.forEach(message->paymentResponseMessageListener.completeCoupon(message));
    }
}
