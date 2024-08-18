package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.ProductDetails;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {

}
