package com.project.coupon_service.message.consumer;


import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.UserCreatedRequest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedConsumer {

        
    private final UserMessageListener userMessageListener;
    
      @KafkaListener(id = "user-2", topics = "user-created") 
    public void received(List<UserCreatedRequest> messages) {    

      
                  
      
      //  messages.forEach(userCreated ->
      //    log.info("User Id : {}", userCreated));

         messages.forEach(userCreated -> 
         userMessageListener.userCreated(userCreated) );
        
    }
}
