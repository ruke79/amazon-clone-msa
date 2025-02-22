package com.project.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.userservice.model.PaymentResult;


@Repository
public interface PaymentRepository extends JpaRepository<PaymentResult, Long> {

}
