package com.project.chatserver.common.config.interceptor;

import com.project.chatserver.common.exception.handler.ErrorCode;

import com.project.chatserver.common.util.TokenHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final TokenHandler tokenHandler;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // StompCommand에 따라서 로직을 분기해서 처리하는 메서드를 호출한다.
        String accessToken = getAccessToken(accessor);
        if (accessToken == null) {
            throw ErrorCode.missingTokenException();
        }
        String username = verifyAccessToken(accessToken);
        log.info("StompAccessor = {}", accessor);
        handleMessage(accessor.getCommand(), accessor, username);
        return message;
    }

    private void handleMessage(StompCommand stompCommand, StompHeaderAccessor accessor, String username) {
        switch (stompCommand) {
            case CONNECT:
                break;
            case SUBSCRIBE:
            case SEND:
                tokenHandler.getUid(getAccessToken(accessor));
                break;
            case DISCONNECT:
                break;
        }
    }

    private String getAccessToken(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }

    private String verifyAccessToken(String accessToken) {
        return tokenHandler.getUid(accessToken);
    }


}
