package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.ProductColorAttribute;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColorAttribute, Long> {

}
