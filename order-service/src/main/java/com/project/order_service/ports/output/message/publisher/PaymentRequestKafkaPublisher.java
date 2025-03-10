package com.project.order_service.ports.output.message.publisher;



import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.common.outbox.OutboxStatus;
import com.project.order_service.domain.exception.OrderDomainException;

import com.project.order_service.outbox.model.PaymentOutboxEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaPublisher {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    @Transactional
    public void publish(PaymentOutboxEvent outboxEvent,  BiConsumer<PaymentOutboxEvent, OutboxStatus> outboxCallback) {

        PaymentRequest payload = this.getOrderEventPayload(outboxEvent.getPayload(), PaymentRequest.class);

          try {
            CompletableFuture<SendResult<String, PaymentRequest>> kafkaResultFuture = kafkaTemplate.send("order", payload);
            kafkaResultFuture.whenComplete((result, ex) -> {
                if (ex == null) {                                       
                    outboxCallback.accept(outboxEvent, OutboxStatus.COMPLETED);
                }
                else {
                    outboxCallback.accept(outboxEvent, OutboxStatus.FAILED);
                }
            });           
       
    
        } catch(KafkaException e) {

            log.error("Error while sending OrderPaymentEventPayload" +
            " to kafka with order id: {} , error: {}",
                payload.getOrderId(),  e.getMessage());
        }

                
    }

    public <T> T getOrderEventPayload(String payload, Class<T> outputType) {
        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!", outputType.getName(), e);
            throw new OrderDomainException("Could not read " + outputType.getName() + " object!", e);
        }
    }
}
