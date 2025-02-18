package com.project.user-service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.user-service.model.CartProduct;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

        
    Optional<CartProduct> findByProduct_ProductId(long productId);
    Optional<CartProduct> findBy_uid(String uid);
}
