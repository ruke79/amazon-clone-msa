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
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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
    private final RedissonClient redissonClient;
    private final MongoTemplate mongoTemplate; // MongoTemplate 주입

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

    public ChatRoomDto createChatRoom(String roomName, String email) throws Exception {
        String lockKey = "chatroom:create:lock:" + roomName;
        RLock lock = redissonClient.getLock(lockKey);
        ChatRoomDto chatRoomDto = null;

        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    ChatRoom chatRoom = ChatRoom.create(roomName);
                    saveChatRoom(chatRoom);
                    chatRoomDto = chatRoomRepository.saveChatRoom(chatRoom);
                    saveParticipantRoom(chatRoomDto.getRoomId(), email);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new IllegalStateException("Failed to acquire lock for chatroom creation.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while acquiring lock", e);
        }

        return chatRoomDto;
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

    /**
     * MongoDB의 findAndModify를 이용한 원자적 메시지 업데이트 메서드
     * 이 메서드는 예시로, ChatMessage를 수정해야 하는 비즈니스 로직이 있다면 사용 가능합니다.
     */
    public ChatMessage updateMessageAtomically(String messageId, String newContent) {
        Query query = new Query(Criteria.where("id").is(messageId));
        Update update = new Update().set("content", newContent);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);

        // findAndModify는 조회와 수정을 하나의 원자적 연산으로 처리하여 동시성 문제를 방지합니다.
        return mongoTemplate.findAndModify(query, update, options, ChatMessage.class);
    }
}