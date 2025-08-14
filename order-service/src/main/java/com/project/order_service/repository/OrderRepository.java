package com.project.order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.common.constants.OrderStatus;
import com.project.order_service.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // 비관적 잠금: 주문을 조회할 때 다른 트랜잭션이 해당 주문을 변경하지 못하도록 락을 겁니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findByOrderIdAndOrderStatus(Long orderId, OrderStatus status);

    // 낙관적 잠금: 주문 엔티티의 @Version 필드를 활용하여 동시성 문제를 해결합니다.
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Order> findByTrackingId(String trackingId);

    Optional<List<Order>> findByCustomerId(Long customerId);
}