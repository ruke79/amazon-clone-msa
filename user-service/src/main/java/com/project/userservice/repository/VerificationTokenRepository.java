package com.project.userservice.repository;

import com.project.userservice.model.VerificationToken;
import com.project.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Date;
import java.util.stream.Stream;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    // --- 비관적 잠금 적용 ---
    // 토큰의 유효성을 검증하고 사용 상태를 변경할 때, 락을 걸어 이중 사용을 막습니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    VerificationToken findByToken(String token);
    
    // --- 낙관적 잠금 적용 ---
    @Lock(LockModeType.OPTIMISTIC)
    VerificationToken findByUser(User user);

    Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}