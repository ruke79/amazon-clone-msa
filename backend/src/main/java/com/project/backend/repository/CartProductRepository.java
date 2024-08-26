package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.CartProduct;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

        
}
