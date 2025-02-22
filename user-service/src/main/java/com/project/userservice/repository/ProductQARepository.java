package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.ProductQA;


@Repository
public interface ProductQARepository extends JpaRepository<ProductQA, Long> {

}
