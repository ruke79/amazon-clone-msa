package com.project.pay_service.outbox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.project.common.outbox.OutboxStatus;
import com.project.pay_service.outbox.model.OrderOutboxEvent;




public interface OrderOutboxRepository extends CrudRepository<OrderOutboxEvent, String> {

    Optional<List<OrderOutboxEvent>> findByOutboxStatus(OutboxStatus outboxStatus);

    Optional<OrderOutboxEvent> findByAggregateIdAndOutboxStatus(String id, OutboxStatus outboxStatus);
}
