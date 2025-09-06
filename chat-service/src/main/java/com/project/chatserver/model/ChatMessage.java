package com.project.chatserver.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import com.project.chatserver.constants.MessageType;

import jakarta.persistence.Column;
import  jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

@Document(collection="chat")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//MongoDB 채팅 모델
public class ChatMessage {

    @Version
    @Column(name = "version")
    private Long version;

    @Id 
    private String id;
    
    private MessageType type;    
    
    private String roomId;        
    private String email;
    private String nickname;
               
    private String content;    

    private String imageUrl;
        
    private Integer readCount;
    
    private String createdAt;

    private ChatMessage(MessageType type, String nickname, String email, String roomId, String content, String imageUrl, Integer readCount,  String createdAt) {
        this.type = type;
        this.nickname = nickname;     
        this.email = email;           
        this.roomId = roomId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;        
        this.readCount = readCount;
    }

    public static ChatMessage get(MessageType type, String nickname, String email, String roomId, String content, String imageUrl, Integer readCount, String createdAt) {
        return new ChatMessage(type, nickname, email, roomId, content, imageUrl, readCount, createdAt);
    }
}
