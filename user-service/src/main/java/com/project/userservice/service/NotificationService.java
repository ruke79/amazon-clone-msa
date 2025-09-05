package com.project.userservice.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userservice.model.Notification;
import com.project.userservice.model.User;
import com.project.userservice.repository.NotificationRepository;
import com.project.userservice.security.jwt.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private final NotificationRepository notificationRepository;

    private final JwtUtils jwtUtils;

    private String sessionId;
        
    public SseEmitter createEmitter(String token) {

        sessionId = jwtUtils.getSessionIdFromJwtToken(token);
        log.info("sessionId : {}", sessionId);

        SseEmitter emitter = new SseEmitter();
        userEmitters.put(sessionId, emitter);
        
        emitter.onCompletion(() -> userEmitters.remove(sessionId));
        emitter.onTimeout(() -> userEmitters.remove(sessionId));
        emitter.onError((e) -> userEmitters.remove(sessionId));

           // SSE 연결이 성공적으로 이루어졌음을 알리는 초기 이벤트 전송
        try {
            emitter.send(SseEmitter.event().name("init").data("connection established"));
            log.info("Initial SSE event sent for session: {}", sessionId);
        } catch (IOException e) {
            log.error("Failed to send initial SSE event: {}", e.getMessage());
            emitter.completeWithError(e); // 예외 발생 시 Emitter 종료
        }
        
        return emitter;
    }
    
    
    public void sendNotification(String userId, String message) {
        Notification notification = new Notification(message, userId, sessionId);
        Notification savedNotification = notificationRepository.save(notification);
        publishNotification(savedNotification);
    }
    
    private void publishNotification(Notification notification) {
        Long num = redisTemplate.convertAndSend("sse", notification);
        log.info("SSE Msg num: {}  Session Id : {}", num, notification.getSessionId());
    }
    
    private void sendRealTimeNotification(Notification notification) {
        SseEmitter emitter = userEmitters.get(notification.getSessionId());
        if (emitter != null) {
            executor.execute(() -> {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(notification.getMessage()));
                } catch (Exception e) {
                    // 예외 처리
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }
    
    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, pattern) -> {
            String body = redisTemplate.getStringSerializer().deserialize(message.getBody());
            Notification notification = null;
            try {
                notification = objectMapper.readValue(body, Notification.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            
            assert notification != null;
            
            sendRealTimeNotification(notification);
        }, new PatternTopic("sse"));
        
        return container;
    }
}


