package com.project.order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import com.project.order_service.model.OrderProduct;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

    // 비관적 잠금: 주문 상품 목록을 조회할 때 락을 걸어 다른 트랜잭션의 접근을 막습니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from OrderProduct p where p.order.orderId = :orderId")
    Optional<List<OrderProduct>> findByOrder_OrderId(Long orderId);

    // 낙관적 잠금: OrderProduct 엔티티의 @Version 필드를 활용합니다.
    @Lock(LockModeType.OPTIMISTIC)
    @Override
    Optional<OrderProduct> findById(Long id);
}