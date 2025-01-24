package com.project.backend.message.repository;

import org.springframework.data.repository.CrudRepository;

import com.project.backend.message.model.OutboxEvent;

public interface OutboxEventRepository extends CrudRepository<OutboxEvent, String> {
}
