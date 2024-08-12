package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    

}
