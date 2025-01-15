package com.project.chatserver.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import  jakarta.persistence.Id;
import java.time.LocalDateTime;

@Document(collection="chatmessage")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//MongoDB 채팅 모델
public class ChatMessage {
    private MessageType type;

    private Long senderId;
    
    private String roomId;

    private String message;

    // @CreatedDate
    private String createdAt;

    //private String imageUrl;

    private ChatMessage(MessageType type, Long senderId,  String roomId, String message) {
        this.type = type;
        this.senderId = senderId;        
        this.roomId = roomId;
        this.message = message;
    }

    public static ChatMessage get(MessageType type, Long senderId, String roomId, String message) {
        return new ChatMessage(type, senderId, roomId, message);
    }
}
