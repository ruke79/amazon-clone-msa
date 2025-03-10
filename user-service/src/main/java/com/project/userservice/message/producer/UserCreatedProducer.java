package com.project.userservice.message.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.UserCreatedRequest;




@Component
@RequiredArgsConstructor
public class UserCreatedProducer {

    private final KafkaTemplate<String, UserCreatedRequest> kafkaTemplate;


    public void publish(UserCreatedRequest UserCreatedRequest) {
        kafkaTemplate.send("user-created", UserCreatedRequest);
    }
}
