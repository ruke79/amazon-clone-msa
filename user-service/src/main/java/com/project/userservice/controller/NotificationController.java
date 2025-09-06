package com.project.userservice.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.project.userservice.constants.TokenType;
import com.project.userservice.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    
    @GetMapping(value = "/sse", produces = "text/event-stream; charset=utf-8")
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserDetails userDetails, 
    HttpServletRequest request, HttpServletResponse response) {

        log.info("SSE 연결 시도"); // 메소드 진입 로그
        log.info("Spring Security UserDetails: {}", userDetails != null ? userDetails.getUsername() : "null");

        String accessToken = request.getHeader("Authorization");
        log.info("Request Authorization Header: {}", accessToken);
               
        if (accessToken == null) {
            log.warn("Authorization 헤더가 누락되었습니다. null을 반환합니다.");
            return null;
        }     

        response.setHeader("X-Accel-Buffering", "no");        
        response.setHeader("Cache-Control", "no-cache");
        
        String sessionId = accessToken.substring(7);
        log.info("토큰에서 추출한 Session ID: {}", sessionId);

        return notificationService.createEmitter(accessToken.substring(7));
    }


}