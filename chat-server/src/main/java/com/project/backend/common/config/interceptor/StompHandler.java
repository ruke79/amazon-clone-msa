package com.project.chatserver.common.config.interceptor;

import com.project.chatserver.common.exception.handler.ErrorCode;

import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.service.ChatService;

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
    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // StompCommand에 따라서 로직을 분기해서 처리하는 메서드를 호출한다.
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String accessToken = getAccessToken(accessor);
            if (accessToken == null) {
                throw ErrorCode.unauthorizedTokenException();
            }
            boolean isValidToken = tokenHandler.verifyToken(accessToken);
            if (!isValidToken) {
                throw ErrorCode.unauthorizedTokenException();
            }
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            String roomId = getRoomId(accessor);
            String userId = getUserId(accessor);

            chatService.connect(roomId, userId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {

            // String roomId = getRoomId(accessor);
            // String userId = getUserId(accessor);
            String userId = (String) message.getHeaders().get("simpSessionId");

            chatService.disconnect(userId);
        }

        return message;
    }

    private String getAccessToken(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }

    private String getUserId(StompHeaderAccessor accessor) {
        return tokenHandler.getUid(getAccessToken(accessor));
    }

    private String getRoomId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("roomId");
    }


}
