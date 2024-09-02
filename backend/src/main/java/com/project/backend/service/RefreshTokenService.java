package com.project.backend.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.backend.exceptionHandling.TokenRefreshException;
import com.project.backend.model.RefreshToken;
import com.project.backend.model.User;
import com.project.backend.repository.RefreshTokenRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.jwt.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefreshTokenService {

  @Value("${spring.app.jwtRefreshExpirationMs}")
  private Long refreshTokenDurationMs;

    
  private final RefreshTokenRepository refreshTokenRepository;
  
  
  private final UserRepository userRepository;

  private final JwtUtils jwtUtils;

  
  @Autowired
  public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
      JwtUtils jwtUtils) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
    this.jwtUtils = jwtUtils;
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken createRefreshToken(Long userId) {
    RefreshToken refreshToken = new RefreshToken();

    User user = userRepository.findById(userId)
    .orElseThrow(() -> new RuntimeException("User not found"));

    refreshTokenRepository.deleteByUser(user);


    refreshToken.setUser(user);    
    //refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
   
    
    refreshToken.setToken(jwtUtils.generateRefreshTokenFromEmail(user));

    SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
    
    String formattedDate = formatter.format(jwtUtils.getExpirationFromJwtToken(refreshToken.getToken()));

    log.info(formattedDate);
    
    Date myDate = Date.from(Instant.now().plusMillis(refreshTokenDurationMs));
    
     formattedDate = formatter.format(new Date());

      log.info(formattedDate);
      
      refreshToken.setExpiryDate(myDate);

    Date date = jwtUtils.getExpirationFromJwtToken(refreshToken.getToken());

    Date date2 = new Date();
    
    if (date.getTime() - date2.getTime() > 0) {
      log.info("is true");
    }

    if ( date.before(date2)) {
      log.info("is before");
    }

     formattedDate = formatter.format(date);

      log.info(formattedDate);
      


    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  // public RefreshToken verifyExpiration(RefreshToken token) {
  //   if (token.getExpiryDate().isBefore(Dis)) < 0) {
  //     refreshTokenRepository.delete(token);
  //     throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
  //   }

  //   return token;
  // }

  @Transactional
  public int deleteByUserId(Long userId) {
    User user = userRepository.findById(userId)
    .orElseThrow(() -> new RuntimeException("User not found"));
    return refreshTokenRepository.deleteByUser(user);
  }

}
