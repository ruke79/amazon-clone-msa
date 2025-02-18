package com.project.catalog_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.catalog_service.model.ProductColorAttribute;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColorAttribute, Long> {

}
