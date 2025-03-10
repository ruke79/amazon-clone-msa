package com.project.order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.constants.OrderStatus;
import com.project.order_service.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    
    Optional<Order> findByTrackingId(String trackingId);

    Optional<List<Order>> findByCustomerId(Long customerId);
    
    Optional<Order> findByOrderIdAndOrderStatus(Long orderId, OrderStatus status);
}
