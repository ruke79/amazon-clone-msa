package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.ProductColorAttribute;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColorAttribute, Long> {

}
