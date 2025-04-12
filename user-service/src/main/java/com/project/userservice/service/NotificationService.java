package com.project.userservice.service;

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

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        userEmitters.put(sessionId, emitter);
        
        emitter.onCompletion(() -> userEmitters.remove(sessionId));
        emitter.onTimeout(() -> userEmitters.remove(sessionId));
        emitter.onError((e) -> userEmitters.remove(sessionId));
        
        return emitter;
    }
    
    @Transactional
    public void sendNotification(String userId, String message) {
        Notification notification = new Notification(message, userId, sessionId);
        Notification savedNotification = notificationRepository.save(notification);
        publishNotification(savedNotification);
    }
    
    private void publishNotification(Notification notification) {
        redisTemplate.convertAndSend("sse", notification);
    }
    
    private void sendRealTimeNotification(Notification notification) {
        SseEmitter emitter = userEmitters.get(notification.getSessionId());
        if (emitter != null) {
            executor.execute(() -> {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(notification.getMessage()));
                } catch (Exception e) {
                    // 예외 처리
                    log.error("SSE Error: {}", e.getStackTrace());
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


