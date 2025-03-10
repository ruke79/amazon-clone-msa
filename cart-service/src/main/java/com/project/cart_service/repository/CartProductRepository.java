package com.project.cart_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.cart_service.model.CartProduct;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

        
    Optional<CartProduct> findByProductId(long productId);
    Optional<CartProduct> findBy_uid(String uid);
    
}
