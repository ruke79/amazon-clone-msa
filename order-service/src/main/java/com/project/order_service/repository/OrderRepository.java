package com.project.order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

     // --- 페이지 기반 조회 메서드 (Pageable) ---
    /**
     * 고객 ID로 주문 목록을 페이지 단위로 조회합니다.
     * @param customerId 조회할 고객의 ID
     * @param pageable 페이지 정보 (PageRequest 객체)
     * @return Order 엔티티의 페이지 객체
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    // --- 커서 기반 조회 메서드 (Cursor-based Pagination) ---
    /**
     * 특정 고객의 주문 목록을 커서를 기준으로 페이지 단위로 조회합니다.
     * 커서는 마지막으로 조회한 주문 ID를 의미하며, 이 ID보다 작은 주문을 가져옵니다.
     * @param customerId 조회할 고객의 ID
     * @param cursorId 마지막으로 조회한 주문 ID
     * @param pageable 페이지 크기 정보
     * @return Order 엔티티의 페이지 객체
     */
    Page<Order> findByCustomerIdAndOrderIdLessThanOrderByOrderIdDesc(Long customerId, Long cursorId, Pageable pageable);

    /**
     * 커서를 사용한 첫 페이지 조회용 메서드 (cursorId가 없을 때 사용).
     * @param customerId 조회할 고객의 ID
     * @param pageable 페이지 크기 정보
     * @return Order 엔티티의 페이지 객체
     */
    Page<Order> findByCustomerIdOrderByOrderIdDesc(Long customerId, Pageable pageable);
}