package com.project.userservice.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.userservice.message.producer.UserCreatedProducer;
import com.project.userservice.model.User;
import com.project.userservice.repository.RoleRepository;
import com.project.userservice.repository.UserRepository;


import com.project.userservice.security.jwt.JwtUtils;
import com.project.userservice.security.response.LoginResponse;
import com.project.userservice.security.service.UserDetailsImpl;
import com.project.userservice.security.service.UserDetailsServiceImpl;
import com.project.userservice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@RestController
@Controller
@RequiredArgsConstructor
public class RegistrationController {

     
      
    private final UserService userService;
         

    @Value("${frontend.url}")
    private String frontendUrl;


    @GetMapping("/user-service/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token) throws JsonProcessingException {

        final String result = userService.validateVerificationToken(token);        
        final User user = userService.getUser(token);
        log.info("User object class: {}", user.getClass().getName()); //proxy class 확인용 로그

        log.info("Before accessing user's email");
        String email = user.getEmail();
        log.info("After accessing user's email: {}", email);
        
        if ("valid".equals(result)) {
            userService.registerConfirmed(user);
            log.info("Account verified for user: {}", user.getUsername());
            
            // 성공 시 프론트엔드 URL로 직접 리다이렉션
            return new ModelAndView("redirect:" + frontendUrl + "/signin", model);
        }

        userService.deleteUser(user);
        log.warn("Account verification failed for token: {}", token);
        
        // 실패 시 프론트엔드의 다른 URL로 리다이렉션하거나, 실패 메시지를 포함
        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return new ModelAndView("redirect:" + frontendUrl + "/verification-failed", model);
    }

    // Rest API 버전 (프론트엔드와 통신용)
    // 프론트엔드가 이 엔드포인트를 호출하고, JSON 응답을 받도록 구현
    // 프론트엔드에서 리다이렉션 처리    
    // @GetMapping("/registrationConfirm")
    // public ResponseEntity<Map<String, String>> confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token)  throws JsonProcessingException {

    //     //Locale locale = request.getLocale();
    //     //model.addAttribute("lang", locale.getLanguage());
    //     final String result = userService.validateVerificationToken(token);
    //     final User user = userService.getUser(token);
        
    //     Map<String, String> response = new HashMap<>();
                
    //     if ("valid".equals(result)) {
    //         userService.registerConfirmed(user);
    //         log.info("Account verified for user: {}", user.getUsername());

    //         // 성공 응답 반환: 클라이언트에게 성공 메시지와 리다이렉션 URL을 전달
    //         response.put("status", "success");
    //         response.put("message", "message.accountVerified");
    //         response.put("redirectUrl", frontendUrl + "/signin");
    //         return ResponseEntity.ok(response);
    //     }

    //     userService.deleteUser(user);
    //     log.warn("Account verification failed for token: {}", token);

    //     // 실패 응답 반환: 클라이언트에게 실패 메시지를 전달
    //     response.put("status", "failure");
    //     response.put("message", "auth.message." + result);
    //     return ResponseEntity.badRequest().body(response);
    
    // }      
    

}
