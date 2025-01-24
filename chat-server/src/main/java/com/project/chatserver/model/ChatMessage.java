package com.project.chatserver.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import com.project.chatserver.constants.MessageType;

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

    private String content;

    private Integer readCount;
    
    private LocalDateTime createdAt;
    

    private ChatMessage(MessageType type, Long senderId,  String roomId, String content, Integer readCount,  LocalDateTime createdAt) {
        this.type = type;
        this.senderId = senderId;        
        this.roomId = roomId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ChatMessage get(MessageType type, Long senderId, String roomId, String content, Integer readCount, LocalDateTime createdAt) {
        return new ChatMessage(type, senderId, roomId, content, readCount, createdAt);
    }
}
