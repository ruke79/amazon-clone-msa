package com.project.userservice.repository;

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

import com.project.userservice.model.User;
import com.project.userservice.security.RefreshToken;

import jakarta.transaction.Transactional;

@Transactional    
@Repository
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {   
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByToken(String token);
    
    //int deleteByUser(User user);    
    int deleteByToken(String stoken);

}
// public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
// }
// public class RefreshTokenRepository {

//   @Value("${spring.app.jwtRefreshExpirationMs}")
//   private int jwtRefreshExpirationMs;

//     private final RedisTemplate redisTemplate;

//     public RefreshTokenRepository(final RedisTemplate redisTemplate) {
//         this.redisTemplate = redisTemplate;
//     }

//      public void save(final RefreshToken refreshToken) {
//         redisTemplate.opsForValue().set(refreshToken.getToken(), refreshToken.getUserId());
//         redisTemplate.expire(refreshToken.getToken(), jwtRefreshExpirationMs, TimeUnit.MILLISECONDS);
//     }

//     public Optional<RefreshToken> findByToken(final String refreshToken) {
//         ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
//         Long userId = valueOperations.get(refreshToken);

//         if (Objects.isNull(userId)) {
//             return Optional.empty();
//         }

//         return Optional.of(new RefreshToken(refreshToken, userId));
//     }

//     public void delete(final RefreshToken refreshToken) {
//         redisTemplate.delete(refreshToken.getToken());
//     }
//}
