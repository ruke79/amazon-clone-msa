package com.project.userservice.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    
    @GetMapping(value = "/sse") // produces = "text/event-stream; charset=utf-8")
    public SseEmitter streamNotifications(@AuthenticationPrincipal UserDetails userDetails, 
    HttpServletRequest request, HttpServletResponse response) {

        String accessToken = request.getHeader("Authorization");

               
        if (accessToken == null) {
             return null;
         }     

        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");

        return notificationService.createEmitter(accessToken.substring(7));
    }


}
