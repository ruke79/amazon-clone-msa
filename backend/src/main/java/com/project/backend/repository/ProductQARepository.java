package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.ProductQA;


@Repository
public interface ProductQARepository extends JpaRepository<ProductQA, Long> {

}
