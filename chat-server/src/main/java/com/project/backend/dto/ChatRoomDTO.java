package com.project.chatserver.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ChatRoomDTO implements Serializable {
    private String roomId;
    private String name;    
}