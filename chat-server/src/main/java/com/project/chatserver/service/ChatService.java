package com.project.chatserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chatserver.client.UserServiceClient;
import com.project.chatserver.common.util.KafkaUtil;
import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.dto.ChatRoomDto;
import com.project.chatserver.dto.MessageDto;

import com.project.chatserver.dto.response.RoomMessagesResponse;
import com.project.chatserver.message.producer.MessageProducer;
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
@Transactional
public class ChatService {

    @PersistenceContext
    private EntityManager em;

        
    private final ChatMessageRepository chatMessageRepository;
    private final MessageProducer messageProducer;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberService chatMemberService;
    
    private final SequenceGeneratorService sequenceGenerator;

    public void connect(String roomId, String email, String userSessionId ) {

                
        ChatMember member = saveParticipantRoom(roomId, email);

        chatRoomRepository.setUserEnterInfo(userSessionId, roomId);

        // send 입장 메시지
        
    }

    public void disconnect( String email) {

        String roomId = chatRoomRepository.getUserEnterRoomId(email);

        // create quit message
        // sendMessage(qit meesage);

        chatRoomRepository.removeUserEnterInfo(email);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getRoomMessages(String roomId) {
        List<ChatMessage> chatList = chatMessageRepository.findByRoomId(roomId).orElse(Collections.emptyList());

        return chatList.stream()
			.map(MessageDto::from)
			.sorted(Comparator.comparing(MessageDto::getCreatedAt))
			.collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getRoomMessagesByCurser(String roomId, String cursorId) {
        
         PageRequest pageable = PageRequest.of(0, 20 + 1);
        Slice<ChatMessage> chatList;
        
        if ( cursorId.equals("-1") ) {
            chatList = chatMessageRepository.findAllByRoomIdOrderByIdDesc(roomId, pageable);        
            log.info("chatlist: " + chatList.getNumberOfElements());
        }
        else  {
            chatList = chatMessageRepository.findByIdLessThanAndRoomIdOrderByIdDescCreatedAtDesc(cursorId, roomId, pageable);
        }

        return chatList.stream()
			.map(MessageDto::from)
			//.sorted(Comparator.comparing(MessageDto::getCreatedAt))
			.collect(Collectors.toList());        
    }

    public String saveMessage(MessageDto message) {

        ChatMessage msg = chatMessageRepository.save(MessageDto.toChatMessage(message));
        return msg.getContent();
    }


    public void sendMessage(MessageDto message) {

        String email = message.getEmail();        
        
        ChatMember member = chatMemberService.findByEmail(email);
        if (null != member) {

            
            message.setId(sequenceGenerator.generateSequence(MessageDto.SEQUENCE_NAME));

            log.info("Message Id  : " + message.getId() );

            message.setSendTimeAndSender(LocalDateTime.now(), member.getNickname(), email, 0);                    
        }       

        messageProducer.send(KafkaUtil.KAFKA_TOPIC, message);
    }

    public List<ChatRoom> fildAllRooms() {

        return chatRoomRepository.findAllRoom();
    }

    public ChatRoomDto  createChatRoom(String roomName, String email) throws Exception {

        ChatRoom chatRoom = ChatRoom.create(roomName);
        saveChatRoom(chatRoom);
        ChatRoomDto ChatRoomDto = chatRoomRepository.saveChatRoom(chatRoom);
        saveParticipantRoom(ChatRoomDto.getRoomId(), email);

        // ResponseEntity<?> response = shoppingServiceClient.findUserByEmail(email);

        // if (response.getStatusCode() == HttpStatus.OK) {
        //     log.info("findUserByEmail");
        // }
        // else 
        //     log.info("Error");

        return ChatRoomDto.builder()
                .name(ChatRoomDto.getName())
                .roomId(ChatRoomDto.getRoomId())                
                .build();
    }

    @Transactional(readOnly = true)
    public void  sendEnterMessage(MessageDto msg, SimpMessageHeaderAccessor headerAccessor) {

        try {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("email", msg.getEmail());
			headerAccessor.getSessionAttributes().put("roomId", msg.getRoomId());
			headerAccessor.getSessionAttributes().put("nickname", msg.getNickname());
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        msg.setContent(msg.getEmail() + " entered.");
        msg.setSendTimeAndSender(LocalDateTime.now(), msg.getNickname(), msg.getEmail(), 0);

        messageProducer.send(KafkaUtil.KAFKA_TOPIC, msg);
    }

    @Transactional(readOnly = true)
    public void  sendLeaveMessage(MessageDto msg, SimpMessageHeaderAccessor headerAccessor) {

        try {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("email", msg.getEmail());
			headerAccessor.getSessionAttributes().put("roomId", msg.getRoomId());
			headerAccessor.getSessionAttributes().put("nickname", msg.getNickname());
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        msg.setContent(msg.getEmail() + " leaved.");
        msg.setSendTimeAndSender(LocalDateTime.now(), msg.getNickname(), msg.getEmail(), 0);

        messageProducer.send(KafkaUtil.KAFKA_TOPIC, msg);
    }


    @Transactional
    public void saveChatRoom(ChatRoom chatRoom) {        
        em.persist(chatRoom);
    }

    private ChatMember saveParticipantRoom(String roomId, String userEmail) {
        
        
        ChatMember member = chatMemberService.findByEmail(userEmail);                        
        
        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId);                                                
        
        ParticipantChatRoom chatRoomInfo = ParticipantChatRoom.get(chatRoom, member);        
        em.persist(chatRoomInfo);

        member.getRoomList().add(chatRoomInfo);
        chatRoom.getRoomList().add(chatRoomInfo);           
        

      
        
        return member;
    }
}
