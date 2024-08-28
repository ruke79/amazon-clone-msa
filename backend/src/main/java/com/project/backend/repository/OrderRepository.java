package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.backend.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional    
    @Query("Update Order o SET o.isPaid = :payStatus WHERE o.orderId = :id")
    int updateIsPaidById(@Param("id") Long id, @Param("payStatus") Boolean payStatus);

    Order findByUser_UserName(String email);
}
