package com.project.chatserver.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import  jakarta.persistence.Id;
import java.time.LocalDateTime;

@Document(collection="chatting")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//MongoDB 채팅 모델
public class ChatMessage {
    private MessageType type;

    private String nickname;

    private String email;

    private String roomCode;

    private String message;

    // @CreatedDate
    private String createdAt;

    private String imageUrl;

    private ChatMessage(MessageType type, String nickname, String email, String roomCode, String message) {
        this.type = type;
        this.nickname = nickname;
        this.email = email;
        this.roomCode = roomCode;
        this.message = message;
    }

    public static ChatMessage of(MessageType type, String nickname, String email, String roomCode, String message) {
        return new ChatMessage(type, nickname, email, roomCode, message);
    }
}
