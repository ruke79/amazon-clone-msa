package com.project.chatserver.model;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import  jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Table(name = "chatroom")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    @OneToMany(mappedBy = "room")
    private List<ParticipantChatRoom> roomList = new ArrayList<ParticipantChatRoom>();

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
