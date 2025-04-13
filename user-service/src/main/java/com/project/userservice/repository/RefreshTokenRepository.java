package com.project.userservice.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.User;
import com.project.userservice.security.RefreshToken;

import jakarta.transaction.Transactional;


@Repository
public class RefreshTokenRepository {

  @Value("${spring.app.jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenRepository(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

     public void save(final RefreshToken refreshToken) {
        redisTemplate.opsForValue().set(refreshToken.getSessionId(), refreshToken.getToken());
        redisTemplate.expire(refreshToken.getToken(), System.currentTimeMillis() + jwtRefreshExpirationMs, TimeUnit.MILLISECONDS);
    }

    public void registerBlacklist(String accessToken, Long expiration) {
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findByUserId(final String sessionId, final String userId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(sessionId);

        if (Objects.isNull(refreshToken)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(sessionId, userId, refreshToken));
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }

}
