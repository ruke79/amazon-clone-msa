package com.project.order_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.order_service.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 비관적 잠금: 고객 정보를 조회할 때 다른 트랜잭션이 수정하지 못하도록 락을 겁니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Customer> findByEmail(String email);

    // 낙관적 잠금: 고객 엔티티의 @Version 필드를 활용하여 충돌을 감지합니다.
    @Lock(LockModeType.OPTIMISTIC)
    @Override
    Optional<Customer> findById(Long id);
}