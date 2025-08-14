package com.project.userservice.repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.project.userservice.security.RefreshToken;

@Repository
public class RefreshTokenRepository {

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_PREFIX = "lock:";

    public RefreshTokenRepository(final StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 분산 락 획득 메서드
    private boolean acquireLock(String key, String lockValue) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, lockValue, 30, TimeUnit.SECONDS); // 30초 TTL
        return Boolean.TRUE.equals(success);
    }

    // 분산 락 해제 메서드
    private void releaseLock(String key, String lockValue) {
        // 락을 해제할 때, 자신이 획득한 락인지 확인하는 것이 중요합니다. (A가 락을 획득했는데, B가 락을 해제하면 안됨)
        String value = redisTemplate.opsForValue().get(LOCK_PREFIX + key);
        if (lockValue.equals(value)) {
            redisTemplate.delete(LOCK_PREFIX + key);
        }
    }

    public void save(final RefreshToken refreshToken) {
        String lockKey = refreshToken.getSessionId();
        String lockValue = UUID.randomUUID().toString();
        
        if (acquireLock(lockKey, lockValue)) {
            try {
                redisTemplate.opsForValue().set(refreshToken.getSessionId(), refreshToken.getToken());
                redisTemplate.expire(refreshToken.getSessionId(), System.currentTimeMillis() + jwtRefreshExpirationMs, TimeUnit.MILLISECONDS);
            } finally {
                releaseLock(lockKey, lockValue);
            }
        } else {
            // 락 획득 실패 시, 재시도 로직이나 예외 처리를 추가할 수 있습니다.
            throw new IllegalStateException("Failed to acquire lock for saving RefreshToken");
        }
    }

    public void registerBlacklist(String accessToken, Long expiration) {
        // 블랙리스트 등록은 락이 필요 없을 수 있습니다.
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
        String lockKey = key;
        String lockValue = UUID.randomUUID().toString();

        if (acquireLock(lockKey, lockValue)) {
            try {
                redisTemplate.delete(key);
            } finally {
                releaseLock(lockKey, lockValue);
            }
        } else {
            // 락 획득 실패 시, 재시도 로직이나 예외 처리를 추가할 수 있습니다.
            throw new IllegalStateException("Failed to acquire lock for deleting RefreshToken");
        }
    }
}