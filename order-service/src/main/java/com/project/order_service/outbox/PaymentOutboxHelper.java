package com.project.order_service.outbox;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.outbox.OutboxStatus;
import com.project.order_service.domain.exception.OrderDomainException;
import com.project.order_service.outbox.model.PaymentOutboxEvent;
import com.project.order_service.outbox.repository.PaymentOutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxHelper {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public void save(PaymentOutboxEvent outboxEvent) {
       PaymentOutboxEvent response = paymentOutboxRepository.save(outboxEvent);
       if (response == null) {
           log.error("Could not save OutboxEvent with outbox id: {}", outboxEvent.getId());
           throw new OrderDomainException("Could not save OutboxEvent with outbox id: " +
           outboxEvent.getId());
       }
       log.info("OutboxEvent saved with outbox id: {}", outboxEvent.getId());
    }

    @Transactional(readOnly = true)
    public Optional<List<PaymentOutboxEvent>> getPaymentOutboxEventByOutboxStatus(OutboxStatus status) {

        return paymentOutboxRepository.findByOutboxStatus(status);        
    }


    @Transactional
    public void savePaymentOutbox(PaymentRequest request, OutboxStatus status)  {

        String payload = this.createPayload(request);

        PaymentOutboxEvent outboxEvent = new PaymentOutboxEvent(Long.toString(request.getOrderId()), "Order", "payment-request", payload, status);

        
        this.save(outboxEvent);
    }


      private String createPayload(PaymentRequest payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Could not create PayRequest object for order id: {}",
            payload.getOrderId(), e);
            throw new OrderDomainException("Could not create PayRequest object for order id: " +
            payload.getOrderId(), e);
        }
    }



    
    
    

}
