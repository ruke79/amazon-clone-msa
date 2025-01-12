package com.project.chatserver.dto.chat;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import  jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@RedisHash(value = "chatRoom")
public class ChatRoom {
    @Id
    private String id;

    @Indexed
    private Integer chatroomNo;

    @Indexed
    private String username;

    private List<ChatUser> connectedUsers = new ArrayList<>();


}
