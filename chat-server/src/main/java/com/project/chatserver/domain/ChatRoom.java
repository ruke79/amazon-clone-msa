package com.project.chatserver.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import  jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Table(name = "chatroom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    public static ChatRoom create(String name){
        return get(name);
    }

    private ChatRoom(String roomName) {
        this.roomId = UUID.randomUUID().toString();
        this.roomName = roomName;
    }

    public static ChatRoom get(String roomName) {
        return new ChatRoom(roomName);
    }
}
