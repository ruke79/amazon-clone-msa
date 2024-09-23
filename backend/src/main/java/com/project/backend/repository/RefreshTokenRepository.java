package com.project.backend.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.RefreshToken;
import com.project.backend.model.User;

import jakarta.transaction.Transactional;

@Repository
// public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//     Optional<RefreshToken> findByToken(String token);

//     @Transactional    
//     int deleteByUser(User user);

//     @Transactional    
//     int deleteByToken(String stoken);

// }
// public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
// }
public class RefreshTokenRepository {

  @Value("${spring.app.jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;

    private final RedisTemplate redisTemplate;

    public RefreshTokenRepository(final RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

     public void save(final RefreshToken refreshToken) {
        redisTemplate.opsForValue().set(refreshToken.getToken(), refreshToken.getUserId());
        redisTemplate.expire(refreshToken.getToken(), jwtRefreshExpirationMs, TimeUnit.MILLISECONDS);
    }

    public Optional<RefreshToken> findByToken(final String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long userId = valueOperations.get(refreshToken);

        if (Objects.isNull(userId)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken, userId));
    }

    public void delete(final RefreshToken refreshToken) {
        redisTemplate.delete(refreshToken.getToken());
    }



}