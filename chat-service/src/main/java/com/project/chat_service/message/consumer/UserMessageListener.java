package com.project.chat_service.message.consumer;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.chat_service.common.exception.UserDomainException;
import com.project.chat_service.model.ChatMember;
import com.project.chat_service.repository.ChatMemberRepository;
import com.project.common.message.dto.request.UserCreatedRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageListener {

    private final ChatMemberRepository chatMemberRepository;

    
    @Transactional
    void userCreated(UserCreatedRequest createdUser) {

        try {
        ChatMember customer = chatMemberRepository.saveAndFlush(ChatMember.builder().
                                id(createdUser.getUserId()).        
                                imageUrl(createdUser.getImage()).                                
                                nickname(createdUser.getNickname()).
                                email(createdUser.getEmail()).
                                build());
        
        if (customer == null) {
            log.error("Member could not be created in chat database with id: {}", createdUser.getUserId());
            throw new UserDomainException("Member could not be created in order database with id " +
            createdUser.getUserId());
        }
        log.info("Customer is created in order database with id: {}", customer.getId());

         }
        catch(DataIntegrityViolationException err) {
            log.error(err.getMessage());
        }


    }
}
