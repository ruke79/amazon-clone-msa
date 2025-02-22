package com.project.order_service.outbox.repository;

import org.springframework.data.repository.CrudRepository;

import com.project.order_service.outbox.model.OutboxEvent;



public interface OutboxEventRepository extends CrudRepository<OutboxEvent, String> {


}
