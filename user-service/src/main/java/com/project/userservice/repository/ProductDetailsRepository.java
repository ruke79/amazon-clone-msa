package com.project.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.ProductDetails;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {

    
    List<ProductDetails> findDistinctAllByProductProductIdIn(List<Long> productIds);
    
    @Query("select distinct d from ProductDetails d")
    List<ProductDetails> findDistinctAll();
}
