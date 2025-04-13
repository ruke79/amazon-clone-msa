package com.project.userservice.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.constants.StatusMessages;
import com.project.userservice.constants.AppRole;
import com.project.userservice.exceptionHandling.TokenRefreshException;
import com.project.userservice.model.User;
import com.project.userservice.repository.RefreshTokenRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.RefreshToken;
import com.project.userservice.security.jwt.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

      
  private final RefreshTokenRepository refreshTokenRepository;
  
  
  private final UserRepository userRepository;

  private final JwtUtils jwtUtils;

    
  public Optional<RefreshToken> findByUserId(String sessionId, String userId) {
    return refreshTokenRepository.findByUserId(sessionId, userId);
  }

  

  public void  deleteByKey(String userId) {
    refreshTokenRepository.delete(userId);
  }

  public void resigterBlacklist(String accessToken, Long expiration) {
    refreshTokenRepository.registerBlacklist(accessToken, expiration);
  }

  public RefreshToken createRefreshToken(Long userId) {
        
    
    User user = userRepository.findById(userId)
    .orElseThrow(() -> new RuntimeException(StatusMessages.USER_NOT_FOUND));

    

    // log.info("refreshTokenRepository.deleteByUser(user) start");
    // refreshTokenRepository.deleteByUser(user);

    // log.info("refreshTokenRepository.deleteByUser(user) End");

    
    // refreshToken.setUser(user);    
    
    // refreshToken.setToken(jwtUtils.generatRefreshTokenFromUser(user));
   
    // Date myDate = Date.from(Instant.now().plusMillis(refreshTokenDurationMs));
      
    // refreshToken.setExpiryDate(myDate);

    // log.info("refreshTokenRepository.save(refreshToken) start");

    // refreshToken = refreshTokenRepository.save(refreshToken);

    // log.info("refreshTokenRepository.save(refreshToken) end");

    // Redis start 
    String token = jwtUtils.generatRefreshTokenFromUser(user);
    String sessionId = jwtUtils.getSessionIdFromJwtToken(token);
    
    RefreshToken refreshToken = new RefreshToken(sessionId, user.getEmail(), token);
    refreshTokenRepository.save(refreshToken);

    // Redis End

    return refreshToken;
  }

  
  // @Transactional  
  // public int deleteByUserId(Long userId) {
  //   User user = userRepository.findById(userId)
  //   .orElseThrow(() -> new RuntimeException(StatusMessages.USER_NOT_FOUND));
  //   return refreshTokenRepository.deleteByUser(user)
  //   ;
  // }

}
