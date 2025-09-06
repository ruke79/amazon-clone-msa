package com.project.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.userservice.model.ShippingAddress;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    // --- 비관적 잠금 적용 ---
    // 특정 사용자의 배송지 정보를 조회할 때 락을 걸어 동시 수정을 방지합니다.
    //@Lock(LockModeType.PESSIMISTIC_WRITE)   ->Transactional ReadOnly true 에서 사용 불가
    //@Lock(LockModeType.OPTIMISTIC) // Transactional ReadOnly true 에서 사용 가능
    List<ShippingAddress> findByUser_UserId(Long userId);
    
    // --- 낙관적 잠금 적용 ---
    @Lock(LockModeType.OPTIMISTIC) // Transactional ReadOnly true 에서 사용 가능
    List<ShippingAddress> findByUser_Username(@Param("username") String username);

    @Lock(LockModeType.OPTIMISTIC) // Transactional ReadOnly true 에서 사용 가능
    Optional<ShippingAddress> findByActive(boolean active);
    
    @Lock(LockModeType.OPTIMISTIC)
    Optional<ShippingAddress> findById(Long id);        
}