package com.project.order_service.ports.input.message.listener;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.UserCreatedRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerKafkaListener {


    private static int seq = 0;
    private final CustomerMessageListener customerMessageListener;

    @KafkaListener(id = "user-1", topics = "user-created") 
    public void received(List<UserCreatedRequest> messages) {

        

        log.info("Order Service : {}", seq++);

         messages.forEach(userCreated ->
          customerMessageListener.customerCreated(userCreated));
        
    }


}
