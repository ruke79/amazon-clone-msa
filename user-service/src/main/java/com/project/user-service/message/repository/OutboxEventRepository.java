package com.project.user-service.message.repository;

import org.springframework.data.repository.CrudRepository;

import com.project.user-service.message.model.OutboxEvent;

public interface OutboxEventRepository extends CrudRepository<OutboxEvent, String> {
}
