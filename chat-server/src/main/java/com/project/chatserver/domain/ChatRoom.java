package com.project.chatserver.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import  jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * mysql에 저장할 chat entity
 * 엔티티에는 @NoArgsConstructor(access = AccessLevel.PROTECTED)
 * @AllArgsConstructor , @Setter지양
 * @DynamicInsert로 수정되는 컬럼만 쿼리
 */
@Entity
@Getter
@Table(name = "chatroom")
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    private String roomCode;

    @Column(nullable = false)
    private String roomName;

    @OneToMany(mappedBy = "room", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ParticipantChatRoom> memberList;

    private ChatRoom(String roomName) {
        this.roomCode = UUID.randomUUID().toString();
        this.roomName = roomName;
    }

    public static ChatRoom of(String roomName) {
        return new ChatRoom(roomName);
    }
}
