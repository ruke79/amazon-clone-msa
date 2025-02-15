package com.project.chatserver.message.producer;




import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.project.chatserver.dto.MessageDTO;

@Slf4j
@Component
public class MessageProducer {


    @Autowired
    private KafkaTemplate<String, MessageDTO> kafkaTemplate;

    // 메시지를 지정한 Kafka 토픽으로 전송
    public void send(String topic, MessageDTO data) {

        // KafkaTemplate을 사용하여 메시지를 지정된 토픽으로 전송
        kafkaTemplate.send(topic, data);
    }
}
