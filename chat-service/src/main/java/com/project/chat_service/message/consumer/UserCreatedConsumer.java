package com.project.chat_service.message.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.UserCreatedRequest;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCreatedConsumer {

    
    private final UserMessageListener userMessageListener;
    
      @KafkaListener(id = "user-3", topics = "user-created", 
      containerFactory = "userCreatedContainerFactory") 
    public void received(List<UserCreatedRequest> messages) {

        
        messages.forEach(userCreated ->
        userMessageListener.userCreated(userCreated));
        
    }
}
