package com.project.chat_service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ChatRoomDto implements Serializable {
    private String roomId;
    private String name;    
}