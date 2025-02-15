package com.project.chatserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chatserver.client.ShoppingServiceClient;
import com.project.chatserver.common.util.KafkaUtil;
import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.dto.ChatRoomDTO;
import com.project.chatserver.dto.MessageDTO;
import com.project.chatserver.dto.ServiceUserDTO;

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

    private final MongoTemplate mongoTemplate;
    private final TokenHandler tokenHandler;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageProducer messageProducer;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberService chatMemberService;
    private final ShoppingServiceClient shoppingServiceClient;
    private final SequenceGeneratorService sequenceGenerator;

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

    @Transactional(readOnly = true)
    public List<MessageDTO> getRoomMessages(String roomId) {
        List<ChatMessage> chatList = chatMessageRepository.findByRoomId(roomId).orElse(Collections.emptyList());

        return chatList.stream()
			.map(MessageDTO::from)
			.sorted(Comparator.comparing(MessageDTO::getCreatedAt))
			.collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getRoomMessagesByCurser(String roomId, String cursorId) {
        //Slice<ChatMessage> chatList = chatMessageRepository.find
         PageRequest pageable = PageRequest.of(0, 20 + 1);
        Slice<ChatMessage> chatList;
        //if ( null == cursorId ) {
        if ( cursorId.equals("-1") ) {
            chatList = chatMessageRepository.findAllByRoomIdOrderByIdDesc(roomId, pageable);        
            log.info("chatlist: " + chatList.getNumberOfElements());
        }
        else  {
            chatList = chatMessageRepository.findByIdLessThanAndRoomIdOrderByIdDescCreatedAtDesc(cursorId, roomId, pageable);
        }

        return chatList.stream()
			.map(MessageDTO::from)
			//.sorted(Comparator.comparing(MessageDTO::getCreatedAt))
			.collect(Collectors.toList());        
    }

    public String saveMessage(MessageDTO message) {

        ChatMessage msg = chatMessageRepository.save(MessageDTO.toChatMessage(message));
        return msg.getContent();
    }


    public void sendMessage(MessageDTO message) {

        String email = message.getEmail();        
        
        ChatMember member = chatMemberService.findByEmail(email);
        if (null != member) {

            
            message.setId(sequenceGenerator.generateSequence(MessageDTO.SEQUENCE_NAME));

            log.info("Message Id  : " + message.getId() );

            message.setSendTimeAndSender(LocalDateTime.now(), member.getNickname(), email, 0);                    
        }       

        messageProducer.send(KafkaUtil.KAFKA_TOPIC, message);
    }

    public List<ChatRoom> fildAllRooms() {

        return chatRoomRepository.findAllRoom();
    }

    public ChatRoomDTO  createChatRoom(String roomName, String email) throws Exception {

        ChatRoom chatRoom = ChatRoom.create(roomName);
        saveChatRoom(chatRoom);
        ChatRoomDTO chatRoomDto = chatRoomRepository.saveChatRoom(chatRoom);
        saveParticipantRoom(chatRoomDto.getRoomId(), email);

        // ResponseEntity<?> response = shoppingServiceClient.findUserByEmail(email);

        // if (response.getStatusCode() == HttpStatus.OK) {
        //     log.info("findUserByEmail");
        // }
        // else 
        //     log.info("Error");

        return ChatRoomDTO.builder()
                .name(chatRoomDto.getName())
                .roomId(chatRoomDto.getRoomId())                
                .build();
    }

    @Transactional(readOnly = true)
    public void  sendEnterMessage(MessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {

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
    public void  sendLeaveMessage(MessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {

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
        
        log.info("userEmail :" + userEmail);
        ChatMember member = chatMemberService.findByEmail(userEmail);                        
        
        log.info("Member Name : " + userEmail + " " + member.getNickname());

        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId);
                                                
        log.info("ChatRoom name : " + chatRoom.getRoomName());
        log.info("ChatRoom Id : " + chatRoom.getRoomUid());

        ParticipantChatRoom chatRoomInfo = new ParticipantChatRoom();
        chatRoomInfo.setMember(member);
        chatRoomInfo.setRoom(chatRoom);
        em.persist(chatRoomInfo);

        member.getRoomList().add(chatRoomInfo);
        chatRoom.getRoomList().add(chatRoomInfo);

        return member;
    }
}
