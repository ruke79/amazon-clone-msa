package com.project.pay_service.ports.output.message.publisher;



import java.util.function.BiConsumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
            kafkaTemplate.send("payment", payload);

        } catch(Exception e) {

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
