package com.project.chatserver.domain;

@Entity
@Getter
@NoArgsConstructor
public class ParticipantChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private ChatRoom room;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private MemberChatRoom(ChatRoom room, Member member) {
        this.room = room;
        this.member = member;
        // room.getMemberList().add(this);
        // member.getRoomList().add(this);
    }

    public static MemberChatRoom of(ChatRoom room, Member member) {
        return new MemberChatRoom(room, member);
    }
}
