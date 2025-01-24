package com.project.chatserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.dto.ChatRoomDTO;
import com.project.chatserver.model.ChatMember;
import com.project.chatserver.model.ChatMessage;
import com.project.chatserver.model.ChatRoom;
import com.project.chatserver.model.ParticipantChatRoom;
import com.project.chatserver.repository.ChatMessageRepository;
import com.project.chatserver.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class ChatService {

    @PersistenceContext
    private EntityManager em;

    private final MongoTemplate mongoTemplate;
    private final TokenHandler tokenHandler;
    private final ChatMessageRepository mongoChatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberService chatMemberService;

    public void connect(String roomId, String email ) {

        chatRoomRepository.setUserEnterInfo(email, roomId);

        ChatMember member = saveParticipantRoom(roomId, email);

        // send 입장 메시지
        
    }

    public void disconnect( String email) {

        String roomId = chatRoomRepository.getUserEnterRoomId(email);

        // create quit message
        // sendMessage(qit meesage);

        chatRoomRepository.removeUserEnterInfo(email);
    }


    public void sendChatMessage(ChatMessage chatMessage) {

  
    }

    public List<ChatRoom> fildAllRooms() {

        return chatRoomRepository.findAllRoom();
    }

    public ChatRoomDTO  createChatRoom(String roomName, String email) throws Exception {

        ChatRoomDTO chatRoomDto = chatRoomRepository.createChatRoom(roomName);
        saveParticipantRoom(chatRoomDto.getRoomId(), email);

        return ChatRoomDTO.builder()
                .name(chatRoomDto.getName())
                .roomId(chatRoomDto.getRoomId())                
                .build();
    }


    @Transactional
    public void saveChatRoom(ChatRoom chatRoom) {        
        em.persist(chatRoom);
    }

    private ChatMember saveParticipantRoom(String roomId, String userEmail) {
        ChatMember member = chatMemberService.findByEmail(userEmail);                
        log.info("Member Name : " + member.getNickname());

        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId);
                                                
        log.info("ChatRoom name : " + chatRoom.getRoomName());
        log.info("ChatRoom Id : " + chatRoom.getRoomId());

        ParticipantChatRoom chatRoomInfo = new ParticipantChatRoom();
        chatRoomInfo.setMember(member);
        chatRoomInfo.setRoom(chatRoom);
        em.persist(chatRoomInfo);

        member.getRoomList().add(chatRoomInfo);
        chatRoom.getRoomList().add(chatRoomInfo);

        return member;
    }
}
