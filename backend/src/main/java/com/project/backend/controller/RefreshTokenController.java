package com.project.backend.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.constants.TokenType;
import com.project.backend.exceptionHandling.TokenRefreshException;
import com.project.backend.model.RefreshToken;
import com.project.backend.model.User;
import com.project.backend.security.jwt.JwtUtils;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.service.RefreshTokenService;
import com.project.backend.service.impl.UserServiceImpl;

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

    @PostMapping("/cookie/refresh")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request, HttpServletResponse response) {

        

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(TokenType.REFRESH.getType())) {

                refresh = cookie.getValue();

                
            }
        }
        
        if (refresh == null) {

            log.info("refresh token null");

            
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

    //      SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
    //     String formattedDate = formatter.format(jwtUtils.getExpirationFromJwtToken(refresh));

    //   log.info(formattedDate);

    //   formattedDate = formatter.format(new Date());

    //   log.info(formattedDate);



         //expired check
        try {
            jwtUtils.isJwtTokenExpired(refresh);
        } catch (ExpiredJwtException e) {


            log.info("refresh token expired");
            //로그인 페이지 
            //response status code

            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        
        if(!jwtUtils.validateJwtToken(refresh)) {

            log.info("invalid token ");

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        if(!refreshTokenService.findByToken(refresh).isPresent())  {
                //response body
            log.info("invalid token ");

		    return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }


        String email = jwtUtils.getEmailFromJwtToken(refresh);

        User user = userService.findByEmail(email).
                    orElseThrow(() -> new RuntimeException("User not found with email: " + email));       

        String newAccess = jwtUtils.generateTokenFromEmail(user);

        refreshTokenService.deleteByUserId(user.getUserId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        log.info("토큰 재발급 성공");

        response.setHeader(TokenType.ACCESS.getType(), newAccess);
        response.addCookie(jwtUtils.createCookie(TokenType.REFRESH.getType(), refreshToken.getToken(), 24*60*60));
        //response.setHeader(HttpHeaders.SET_COOKIE, jwtUtils.generateRefreshJwtCookie(refreshToken.getToken()).toString());


        return new ResponseEntity<>(HttpStatus.OK);        
    }

    @PostMapping("/cookie/delete")
    public ResponseEntity<?> delete(HttpServletRequest request, HttpServletResponse response) {

          Cookie cookie = jwtUtils.createCookie(TokenType.REFRESH.getType(), null, 0);

          response.addCookie(cookie);

          return new ResponseEntity<>(HttpStatus.OK);   

    }

//     @PostMapping("/cookie/refresh")
//     public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
//         String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

//         if((refreshToken != null) && (refreshToken.length() > 0)) {
//             return refreshTokenService.findByToken(refreshToken)
//             .map(refreshTokenService::verifyExpiration)
//             .map(RefreshToken::getUser)
//             .map(user-> {
//                 ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
            
//             return ResponseEntity.ok()
//                 .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
//                 .body(new MessageResponse("Token is refreshed successfully!"));
//           })
//           .orElseThrow(() -> new TokenRefreshException(refreshToken,
//               "Refresh token is not in database!"));
//     }
    
//     return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
//   }

}
