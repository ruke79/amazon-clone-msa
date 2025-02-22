package com.project.userservice.message.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.userservice.message.dto.UserCreatedDto;


@Component
@RequiredArgsConstructor
public class UserCreatedProducer {

    private final KafkaTemplate<String, UserCreatedDto> kafkaTemplate;


    public void publish(UserCreatedDto userCreatedDto) {
        kafkaTemplate.send("user_created", userCreatedDto);
    }
}
