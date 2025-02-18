package com.project.user-service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.user-service.model.ProductQA;


@Repository
public interface ProductQARepository extends JpaRepository<ProductQA, Long> {

}
