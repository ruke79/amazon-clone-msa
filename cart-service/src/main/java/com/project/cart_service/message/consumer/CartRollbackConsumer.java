package com.project.cart_service.message.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.CartRollbackRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartRollbackConsumer {

    private final CartRollbackMessageListener cartRollbackMessageListener;

    @KafkaListener(topics = "cart-rollback", containerFactory = "cartRollbackContainerFactory")
    public void receive(List<CartRollbackRequest> messages) {
        
        messages.forEach(message->cartRollbackMessageListener.cartRollback(message));
    }
}
