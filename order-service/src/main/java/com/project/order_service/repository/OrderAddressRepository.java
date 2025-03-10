package com.project.order_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.order_service.model.OrderAddress;



@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {


    Optional<OrderAddress> findByOrder_OrderId(Long orderId);

}
