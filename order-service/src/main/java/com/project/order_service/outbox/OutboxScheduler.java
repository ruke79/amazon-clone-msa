package com.project.order_service.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
