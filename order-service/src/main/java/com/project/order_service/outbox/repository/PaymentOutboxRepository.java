package com.project.order_service.outbox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.project.common.outbox.OutboxStatus;
import com.project.order_service.outbox.model.PaymentOutboxEvent;



public interface PaymentOutboxRepository extends CrudRepository<PaymentOutboxEvent, String> {

    Optional<List<PaymentOutboxEvent>> findByOutboxStatus(OutboxStatus status);

}
