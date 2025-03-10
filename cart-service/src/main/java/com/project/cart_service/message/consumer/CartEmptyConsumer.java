package com.project.cart_service.message.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CartEmptyRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEmptyConsumer {

    private final CartEmptyMessageListener cartEmptyMessageListener;

    @KafkaListener(id="cart", topics = "cart-empty", containerFactory = "cartEmptyContainerFactory")
    public void receive(List<CartEmptyRequest> messages) {
        
        messages.forEach(message->cartEmptyMessageListener.cartEmpty(message));
    }
}
