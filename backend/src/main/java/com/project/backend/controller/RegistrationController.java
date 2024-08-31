package com.project.backend.controller;

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

import com.project.backend.model.User;
import com.project.backend.repository.RoleRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.jwt.JwtUtils;
import com.project.backend.security.response.LoginResponse;
import com.project.backend.security.service.UserDetailsImpl;
import com.project.backend.security.service.UserDetailsServiceImpl;
import com.project.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegistrationController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

      
    @Autowired
    UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Value("${frontend.url}")
    private String frontendUrl;



     @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model, @RequestParam("token") final String token) throws UnsupportedEncodingException {

        //Locale locale = request.getLocale();
        //model.addAttribute("lang", locale.getLanguage());
        final String result = userService.validateVerificationToken(token);
        if (result.equals("valid")) {
            final User user = userService.getUser(token);
            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetailsimpl = (UserDetailsImpl) authentication.getPrincipal();

            String jwtToken = jwtUtils.generateTokenFromEmail(userDetailsimpl);
    
            // Collect roles from the UserDetails
            List<String> roles = userDetailsimpl.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
    
            // Prepare the response body, now including the JWT token directly in the body
            LoginResponse response = new LoginResponse(userDetailsimpl.getUsername(),
                    roles, jwtToken);
            
    
            // Return the response entity with the JWT token included in the response body
            //model.addAttribute("messageKey", "message.accountVerified");
            return new ModelAndView("redirect:"+frontendUrl+"/signin", model);            
        }
        
        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return new ModelAndView("redirect:/badUser", model);
    }      

}
