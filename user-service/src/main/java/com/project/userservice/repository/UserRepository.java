package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.userservice.model.ShippingAddress;
import com.project.userservice.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // --- 비관적 잠금 적용 ---
    // 사용자 정보를 수정하기 전에 락을 걸어 다른 트랜잭션의 접근을 막습니다.
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findByName(String name);  // 별명으로 검색
    
    // --- 낙관적 잠금 적용 ---
    // 엔티티의 @Version 필드를 사용하여 동시성 충돌을 감지합니다.
    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    Optional<User> findByUsername(String name);  // 실명으로 검색

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // User와 Roles를 한 번의 쿼리로 가져오기 위해 JOIN FETCH 사용
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    void delete(User user);

    @Query(value = "SELECT a FROM User u LEFT JOIN ShippingAddress a  WHERE u.userId = :userId")
    List<ShippingAddress> findAddressesByUserUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.defaultPaymentMethod = :paymentMethod WHERE u.userId = :userId")
    int updateDefaultPaymentMethod(@Param("userId") Long userId, @Param("paymentMethod") String paymentMethod);
}