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
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    
 
      
    private final UserService userService;
    
     

    @Value("${frontend.url}")
    private String frontendUrl;


    // @Autowired
    //  public RegistrationController(JwtUtils jwtUtils, AuthenticationManager authenticationManager,
    //         UserService userService, UserDetailsServiceImpl userDetailsService) {
    //     this.jwtUtils = jwtUtils;
    //     this.authenticationManager = authenticationManager;
    //     this.userService = userService;
    //     this.userDetailsService = userDetailsService;
    // }



    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token)  throws JsonProcessingException {

        //Locale locale = request.getLocale();
        //model.addAttribute("lang", locale.getLanguage());
        final String result = userService.validateVerificationToken(token);
         
        if (result.equals("valid")) {
            final User user = userService.getUser(token);

            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }

            userService.registerConfirmed(user);
                        
            model.addAttribute("messageKey", "message.accountVerified");
            return new ModelAndView("redirect:"+frontendUrl+"/signin", model);            
        }
        
        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return new ModelAndView("redirect:/badUser", model);
    }      

    

}
