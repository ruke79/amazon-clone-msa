package com.project.chat_service.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chat_service.common.util.TokenHandler;
import com.project.chat_service.constants.MemberRole;
import com.project.chat_service.model.ChatMember;
import com.project.chat_service.repository.ChatMemberRepository;
import com.project.common.message.dto.request.UserCreatedRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;


@Log4j2
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatMemberService {

    @PersistenceContext
    private EntityManager em;
    private final ChatMemberRepository chatMemberRepository;    
    private final TokenHandler tokenHandler;

    
    public ChatMember findByEmail(String email){

        ChatMember member = chatMemberRepository.findByEmail(email).orElse(null);
      
        return member;
    }

    
    public ChatMember authChatMember(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth.substring(7, auth.length());
        String email = tokenHandler.getUid(token);

        return this.findByEmail(email);
    }

}
