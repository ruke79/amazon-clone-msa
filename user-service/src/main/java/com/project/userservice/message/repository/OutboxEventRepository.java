package com.project.userservice.message.repository;

import org.springframework.data.repository.CrudRepository;

import com.project.userservice.message.model.OutboxEvent;

public interface OutboxEventRepository extends CrudRepository<OutboxEvent, String> {
}
