package com.project.userservice.controller;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.constants.StatusMessages;
import com.project.common.constants.TokenStatus;
import com.project.userservice.constants.TokenType;
import com.project.userservice.exceptionHandling.TokenRefreshException;
import com.project.userservice.model.User;
import com.project.userservice.security.RefreshToken;
import com.project.userservice.security.jwt.JwtUtils;
import com.project.userservice.security.response.LoginResponse;
import com.project.userservice.service.RefreshTokenService;
import com.project.userservice.service.impl.UserServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/")
public class RefreshTokenController {

    private final UserServiceImpl userService;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    
    @Autowired
    public RefreshTokenController(UserServiceImpl userService, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        

        if (request.getCookies() == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        
        
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(TokenType.REFRESH.getType())) {

                refresh = cookie.getValue();

                
            }
        }
        
        if (refresh == null) {

         
            log.info("refresh == null");
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        
         //expired check
        try {
            jwtUtils.isJwtTokenExpired(refresh);
        } catch (ExpiredJwtException e) {

            //로그인 페이지 
            //response status code
            log.info(e.getMessage());

            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }



        
        if(jwtUtils.validateJwtToken(refresh) != TokenStatus.VALID ) {

            
            log.info("invalid refresh token");
            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String email = jwtUtils.getIdFromJwtToken(refresh);

        String sessionId = jwtUtils.getSessionIdFromJwtToken(refresh);

        //if(!refreshTokenService.findByUserId(email).isPresent())  {
        if(!refreshTokenService.findByUserId(sessionId, email).isPresent())  {
                //response body
            
             throw new TokenRefreshException(refresh,
                               "Refresh token is not in database!");
        }

        
      
        
        
        User user = userService.findByEmail(email);                    

               ObjectMapper om = new ObjectMapper();

        LoginResponse loginInfo = userService.getLoginInfoByEmail(email);


        String data;
        try {
            data = om.writeValueAsString(loginInfo);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
        

            String newAccess = jwtUtils.generateTokenFromUser(user);

            // delete and create         
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

            log.info("토큰 재발급 성공");

            response.setHeader(TokenType.ACCESS.getType(), newAccess);
            response.addCookie(jwtUtils.createCookie(TokenType.REFRESH.getType(), refreshToken.getToken(), jwtUtils.getJwtRefreshExpirationMs()));
            //response.setHeader(HttpHeaders.SET_COOKIE, jwtUtils.generateRefreshJwtCookie(refreshToken.getToken()).toString());

         } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            response.setStatus(401);
        }

        return new ResponseEntity<>(HttpStatus.OK);        
    }



    // @PostMapping("/token/delete")
    // public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response, 
    // @AuthenticationPrincipal UserDetails userDetails) {

    //     if (null != userDetails ) {
        
    //       Cookie cookie = jwtUtils.createCookie(TokenType.REFRESH.getType(), null, 0);

    //       response.addCookie(cookie);
          
    //       return new ResponseEntity<>(HttpStatus.OK);   
    //     }
    //     else {                    
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body(StatusMessages.USER_NOT_FOUND);
    //     }            

    // }


}
