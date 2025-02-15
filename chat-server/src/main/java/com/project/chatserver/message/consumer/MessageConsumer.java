package com.project.chatserver.message.consumer;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.project.chatserver.common.util.KafkaUtil;
import com.project.chatserver.dto.MessageDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final SimpMessageSendingOperations template;
    

    @KafkaListener(topics = KafkaUtil.KAFKA_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void receiveMessage(MessageDTO message) {
        log.info("전송 위치 = /sub/chat/room/"+ message.getRoomId());
        log.info("채팅 방으로 메시지 전송 = {}", message);


        // 메시지객체 내부의 채팅방번호를 참조하여, 해당 채팅방 구독자에게 메시지를 발송한다.
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
