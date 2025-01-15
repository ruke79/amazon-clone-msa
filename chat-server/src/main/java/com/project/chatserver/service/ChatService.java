package com.project.chatserver.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chatserver.domain.ChatMessage;
import com.project.chatserver.domain.ChatRoom;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class ChatService {

    @PersistenceContext
    private EntityManager em;

    public void sendChatMessage(ChatMessage chatMessage) {

    }

    @Transactional
    public void saveChatRoom(ChatRoom chatRoom) {        
        em.persist(chatRoom);
    }
}
