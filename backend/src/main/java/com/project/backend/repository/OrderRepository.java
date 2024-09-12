package com.project.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.backend.constants.PaymentResultStatus;
import com.project.backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional    
    @Query("Update Order o SET o.isPaid = :payStatus WHERE o.orderNumber = :orderNumber")
    int updateIsPaidByOrderNumber(@Param("orderNumber") String orderNumber, @Param("payStatus") Boolean payStatus);

    Order findByUser_UserName(String email);

    Optional<Order> findByOrderNumber(String orderNumber);
    
    Optional<Order> findByOrderIdAndPaymentResult_PayStatus(Long orderId, PaymentResultStatus payStatus);
}
