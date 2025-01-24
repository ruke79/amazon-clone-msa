package com.project.chatserver.dto.request;


import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.project.chatserver.constants.MessageType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest implements Serializable {

    private String roomId;
    private Long senderId;    
    
    @Enumerated(EnumType.STRING)    
    private MessageType contentType;    
    private String content;    
    
    private Integer readCount;
    private long sendTime;

    public void setSendTimeAndSender(LocalDateTime sendTime, long , Integer readCount) {
        this.senderName = senderName;
        this.sendTime = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();        
        this.readCount = readCount;

    }
    
    
}
