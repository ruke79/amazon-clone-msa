package com.project.chatserver.common.config.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.project.chatserver.common.exception.handler.ErrorCode;
import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.service.ChatService;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component

@Slf4j
public class StompHandler implements ChannelInterceptor {

    // prevent circular referenece
    @Autowired
    private TokenHandler tokenHandler;
    @Autowired
    private ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // StompCommand에 따라서 로직을 분기해서 처리하는 메서드를 호출한다.        

         // Stomp 명령 및 헤더 전체를 로그로 출력하여 문제 파악
        log.info("STOMP Command: {}", accessor.getCommand());
        log.info("Native Headers: {}", accessor.getNativeHeaders());

        if (StompCommand.CONNECT == accessor.getCommand()) {

            
            String accessToken = getAccessToken(accessor);
            
            if (accessToken == null) {
                log.error("accessToken is null : {}", accessToken);
                throw ErrorCode.unauthorizedTokenException();
            }
            boolean isValidToken = tokenHandler.verifyToken(accessToken);
            if (!isValidToken) {
                throw ErrorCode.unauthorizedTokenException();
            }
        } 
        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {

            try {

                String userSessionId = (String) message.getHeaders().get("simpSessionId");

                String roomId = getRoomId(accessor);
                String userId = getUserId(accessor);

                log.info("Subscribe : {}, {} , {}", roomId,  userId, userSessionId);
                

                chatService.connect(roomId, userId, userSessionId);
            } catch (Exception e) {
                log.error("Error during SUBSCRIBE Stomp handling: {}", e.getMessage(), e);
                throw e; // 예외를 다시 던져서 연결을 종료하도록 함
            }


        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            
            String userSessionId = (String) message.getHeaders().get("simpSessionId");
            
            log.info("Disconnect : {} ",  userSessionId);

            chatService.disconnect(userSessionId);
        }
        else if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) {

            String userId = getUserId(accessor);

            log.info("Unscribe : {} ",  userId);

        }

        return message;
    }

    private String getAccessToken(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }

    private String getUserId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("email");
    }

    private String getRoomId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("roomId");
    }


}
