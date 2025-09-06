package com.project.chatserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.chatserver.controller.ChatApiController;
import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.service.ChatService;

@Configuration
public class ChatConfig {

    private final TokenHandler tokenHandler;
    private final ChatService chatService;

    public ChatConfig(TokenHandler tokenHandler, ChatService chatService) {
        this.tokenHandler = tokenHandler;
        this.chatService = chatService;
    }

    @Bean
    public ChatApiController chatApiController() {
        return new ChatApiController(tokenHandler, chatService);
    }
}