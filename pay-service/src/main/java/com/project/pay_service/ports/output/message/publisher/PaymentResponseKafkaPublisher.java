package com.project.pay_service.ports.output.message.publisher;



import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.checkerframework.checker.units.qual.K;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.common.outbox.OutboxStatus;
import com.project.pay_service.exception.PaymentDomainException;
import com.project.pay_service.outbox.model.OrderOutboxEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseKafkaPublisher {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, PaymentResponse> kafkaTemplate;

    public void publish(OrderOutboxEvent outboxEvent,  BiConsumer<OrderOutboxEvent, OutboxStatus> outboxCallback) {

        PaymentResponse payload = this.getOrderEventPayload(outboxEvent.getPayload(), PaymentResponse.class);

        try {
            CompletableFuture<SendResult<String, PaymentResponse>> kafkaResultFuture = kafkaTemplate.send("payment", payload);
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
            throw new PaymentDomainException("Could not read " + outputType.getName() + " object!", e);
        }
    }
}
