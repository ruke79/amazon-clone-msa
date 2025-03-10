package com.project.pay_service.outbox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.common.outbox.OutboxStatus;
import com.project.pay_service.exception.PaymentDomainException;
import com.project.pay_service.outbox.model.OrderOutboxEvent;
import com.project.pay_service.outbox.repository.OrderOutboxRepository;
import com.project.pay_service.ports.output.message.publisher.PaymentResponseKafkaPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxHelper {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;
    //private final PaymentResponseKafkaPublisher paymentResponseMessagePublisher;

    @Transactional
    public void save(OrderOutboxEvent outboxEvent) {
       OrderOutboxEvent response = orderOutboxRepository.save(outboxEvent);
       if (response == null) {
           log.error("Could not save OutboxEvent with outbox id: {}", outboxEvent.getId());
           throw new PaymentDomainException("Could not save OutboxEvent with outbox id: " +
           outboxEvent.getId());
       }
       log.info("OutboxEvent saved with outbox id: {}", outboxEvent.getId());
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderOutboxEvent>> getOrderOutboxEventByOutboxStatus(OutboxStatus status) {

        return orderOutboxRepository.findByOutboxStatus(status);        
    }

    @Transactional(readOnly = true)
    public Optional<OrderOutboxEvent> getCompletedOrderOutboxEvent(String aggregateId, OutboxStatus status) {

        return orderOutboxRepository.findByAggregateIdAndOutboxStatus(aggregateId, status);
    }


    @Transactional
    public void saveOrderOutbox(PaymentResponse response, OutboxStatus outboxStatus)  {

        String payload = this.createPayload(response);

        OrderOutboxEvent outboxEvent = new OrderOutboxEvent(response.getPaymentId().toString(), 
        "Payment", "payment-response", payload, outboxStatus);

        this.save(outboxEvent);

        //paymentResponseMessagePublisher.publish(outboxEvent, this::updateOutboxStatus);
    }


      private String createPayload(PaymentResponse payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Could not create PayRequest object for order id: {}",
            payload.getOrderId(), e);
            throw new PaymentDomainException("Could not create PayRequest object for order id: " +
            payload.getOrderId(), e);
        }
    }

    public void updateOutboxStatus(OrderOutboxEvent outboxEvent, OutboxStatus outboxStatus) {
        outboxEvent.setOutboxStatus(outboxStatus);
        this.save(outboxEvent);
        log.info("OrderOutboxEvent is updated with outbox status: {}", outboxStatus.name());
    }

    
    

}
