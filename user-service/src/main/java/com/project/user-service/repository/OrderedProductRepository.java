package com.project.user-service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.user-service.model.OrderedProduct;

@Repository
public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long> {

}
