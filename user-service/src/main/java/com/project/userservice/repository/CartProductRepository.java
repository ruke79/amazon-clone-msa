package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.CartProduct;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

        
    Optional<CartProduct> findByProduct_ProductId(long productId);
    Optional<CartProduct> findBy_uid(String uid);
}
