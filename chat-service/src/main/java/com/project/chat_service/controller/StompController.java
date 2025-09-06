package com.project.chat_service.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.project.chat_service.common.util.TokenHandler;
import com.project.chat_service.constants.MessageType;
import com.project.chat_service.dto.ChatRoomDto;
import com.project.chat_service.dto.MessageDto;
import com.project.chat_service.dto.request.ChatRoomRequest;
import com.project.chat_service.dto.response.RoomMessagesResponse;
import com.project.chat_service.model.ChatRoom;
import com.project.chat_service.service.ChatService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Log4j2
@RequiredArgsConstructor
@Controller
public class StompController {

    
    private final ChatService chatService;


    @MessageMapping("/chat/message")
    public void sendMessage(@Valid MessageDto request) {
            chatService.sendMessage(request);
    }

    @MessageMapping("/chat/enter")
	public void enterChatRoom( @RequestBody MessageDto msg, SimpMessageHeaderAccessor headerAccessor) {

        chatService.sendEnterMessage(msg, headerAccessor);
    }

    @MessageMapping("/chat/leave")
	public void leaveChatRoom( @RequestBody MessageDto msg, SimpMessageHeaderAccessor headerAccessor) {

        chatService.sendLeaveMessage(msg, headerAccessor);
    }

   
}
