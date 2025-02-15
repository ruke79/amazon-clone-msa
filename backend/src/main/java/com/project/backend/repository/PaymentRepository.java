package com.project.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.model.PaymentResult;


@Repository
public interface PaymentRepository extends JpaRepository<PaymentResult, Long> {

}
