package com.project.catalog_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.catalog_service.model.ProductColor;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {

}
