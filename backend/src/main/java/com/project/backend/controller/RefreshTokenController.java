package com.project.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.constants.TokenType;
import com.project.backend.exception.TokenRefreshException;
import com.project.backend.model.RefreshToken;
import com.project.backend.model.User;
import com.project.backend.security.jwt.JwtUtils;
import com.project.backend.security.response.MessageResponse;
import com.project.backend.service.RefreshTokenService;
import com.project.backend.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/")
public class RefreshTokenController {

    private final UserService userService;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    @Autowired
    public RefreshTokenController(UserService userService, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(TokenType.REFRESH.getType())) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

         //expired check
        try {
            jwtUtils.isJwtTokenExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        if(!jwtUtils.validateJwtToken(refresh)) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        if(!refreshTokenService.findByToken(refresh).isPresent())  {
                //response body
		    return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }


        String email = jwtUtils.getEmailFromJwtToken(refresh);

        User user = userService.findByEmail(email);       

        String newAccess = jwtUtils.generateTokenFromEmail(user);

        refreshTokenService.deleteByUserId(user.getUserId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        response.setHeader(TokenType.ACCESS.getType(), newAccess);
        response.addCookie(jwtUtils.createCookie(TokenType.REFRESH.getType(), refreshToken.getToken(), 24*60*60));

        return new ResponseEntity<>(HttpStatus.OK);        
    }

    @PostMapping("/cookie/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user-> {
                ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Token is refreshed successfully!"));
          })
          .orElseThrow(() -> new TokenRefreshException(refreshToken,
              "Refresh token is not in database!"));
    }
    
    return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
  }

}
