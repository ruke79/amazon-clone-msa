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
@RestController
@RequiredArgsConstructor
public class RegistrationController {

     
      
    private final UserService userService;
         

    @Value("${frontend.url}")
    private String frontendUrl;


    
    @GetMapping("/registrationConfirm")
    public ResponseEntity<Map<String, String>> confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token)  throws JsonProcessingException {

        //Locale locale = request.getLocale();
        //model.addAttribute("lang", locale.getLanguage());
        final String result = userService.validateVerificationToken(token);
        final User user = userService.getUser(token);
        
        Map<String, String> response = new HashMap<>();
         
        // if (result.equals("valid")) {
            
        //     // if (user.isUsing2FA()) {
        //     // model.addAttribute("qr", userService.generateQRUrl(user));
        //     // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
        //     // }

        //     userService.registerConfirmed(user);

            
                        
        //     model.addAttribute("messageKey", "message.accountVerified");
        //     return new ModelAndView("redirect:"+frontendUrl+"/signin", model);            
        // }

        // userService.deleteUser(user);
        
        // model.addAttribute("messageKey", "auth.message." + result);
        // model.addAttribute("expired", "expired".equals(result));
        // model.addAttribute("token", token);
        // return new ModelAndView("redirect:/badUser", model);

        
        if ("valid".equals(result)) {
            userService.registerConfirmed(user);
            log.info("Account verified for user: {}", user.getUsername());

            // 성공 응답 반환: 클라이언트에게 성공 메시지와 리다이렉션 URL을 전달
            response.put("status", "success");
            response.put("message", "message.accountVerified");
            response.put("redirectUrl", frontendUrl + "/signin");
            return ResponseEntity.ok(response);
        }

        userService.deleteUser(user);
        log.warn("Account verification failed for token: {}", token);

        // 실패 응답 반환: 클라이언트에게 실패 메시지를 전달
        response.put("status", "failure");
        response.put("message", "auth.message." + result);
        return ResponseEntity.badRequest().body(response);
    
    }      
    

}
