package com.project.pay_service.outbox.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

import com.project.common.constants.PaymentStatus;
import com.project.common.outbox.OutboxStatus;


@Entity
@Setter
@Getter
@Table(name = "order_outbox_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderOutboxEvent {
    @Id
    private String id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus outboxStatus;

    

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public OrderOutboxEvent(String aggregateId, String aggregateType, String eventType, String payload, OutboxStatus status) {
        this.id = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.outboxStatus = status;
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderOutboxEvent that = (OrderOutboxEvent) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
