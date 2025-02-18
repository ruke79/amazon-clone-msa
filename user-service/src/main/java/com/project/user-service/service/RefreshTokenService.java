package com.project.user-service.service;

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

import com.project.user-service.constants.AppRole;
import com.project.user-service.constants.StatusMessages;
import com.project.user-service.exceptionHandling.TokenRefreshException;
import com.project.user-service.model.User;
import com.project.user-service.repository.RefreshTokenRepository;
import com.project.user-service.repository.UserRepository;
import com.project.user-service.security.RefreshToken;
import com.project.user-service.security.jwt.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  @Value("${spring.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

    
  private final RefreshTokenRepository refreshTokenRepository;
  
  
  private final UserRepository userRepository;

  private final JwtUtils jwtUtils;

    
  public Optional<RefreshToken> findByUserId(Long userId) {
    return refreshTokenRepository.findByUserId(userId);
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public int  deleteByToken(String token) {
    return refreshTokenRepository.deleteByToken(token);
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
    
    RefreshToken refreshToken = new RefreshToken(jwtUtils.generatRefreshTokenFromUser(user), userId );
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
