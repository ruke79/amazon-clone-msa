package com.project.chat_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.chat_service.model.ChatMessage;



@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Optional<List<ChatMessage>> findByRoomId(String roomId);

    Optional<List<ChatMessage>> findByRoomIdAndContentContaining(String roomId, String content);
        
    Slice<ChatMessage> findAllByRoomIdOrderByIdDesc(String roomId, Pageable page);

    Slice<ChatMessage> findByIdLessThanAndRoomIdOrderByIdDescCreatedAtDesc (String cursorId, String roomId,  Pageable page);


}