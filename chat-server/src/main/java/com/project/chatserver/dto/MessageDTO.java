package com.project.chatserver.dto;


import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.project.chatserver.constants.MessageType;
import com.project.chatserver.model.ChatMessage;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MessageDto implements Serializable {

    @Transient
    public static final String SEQUENCE_NAME = "message_sequence";
    
    @Id    
    private String id;
    private MessageType type;        
    private String roomId;        
    private String email;
    private String nickname;
               
    private String content;    

    private String imageUrl;
        
    private Integer readCount;
    
    private Long createdAt;

    public static MessageDto from(ChatMessage chat) {

        

        return new MessageDto(
            chat.getId(),
			chat.getType(),
			chat.getRoomId(),
            chat.getEmail(),
            chat.getNickname(),						
			chat.getContent(),            
			chat.getImageUrl(),
            chat.getReadCount(), 
            Long.parseLong(chat.getCreatedAt())            
		);

    }

    public static ChatMessage toChatMessage(MessageDto chat) {

        return ChatMessage.get(
            chat.getType(),
			chat.getRoomId(),
            chat.getEmail(),
            chat.getNickname(),						
			chat.getContent(),            
			chat.getImageUrl(),
            chat.getReadCount(),
            Long.toString(chat.getCreatedAt())
        );
    }

    public void setSendTimeAndSender(LocalDateTime sendTime, String nickName, String email, Integer readCount) {
        this.email = email;
        this.nickname = nickName;
        this.createdAt = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();        
        this.readCount = readCount;
    }
    
    
}
