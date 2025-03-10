package com.project.order_service.ports.input.message.listener;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.constants.PaymentStatus;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.order_service.domain.exception.OrderNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResponseKafaListener {

    private final PaymentResponseMessageListener paymentResponseMessageListener;

    @KafkaListener(id = "payment-response", topics = "payment", containerFactory = "paymentResponseListenerContainerFactory")
    public void received(List<PaymentResponse> messages) {

        messages.forEach(paymentResponse -> {
            try {
                if (PaymentStatus.COMPLETED == paymentResponse.getPaymentStatus()) {
                    log.info("Processing successful payment for order id: {}", paymentResponse.getOrderId());
                    paymentResponseMessageListener.paymentCompleted(paymentResponse);
                } else if (PaymentStatus.CANCELED == paymentResponse.getPaymentStatus() ||
                        PaymentStatus.FAILED == paymentResponse.getPaymentStatus()) {
                    log.info("Processing unsuccessful payment for order id: {}", paymentResponse.getOrderId());
                    paymentResponseMessageListener.paymentCancelled(paymentResponse);
                }
            } catch (OptimisticLockingFailureException e) {
                // NO-OP for optimistic lock. This means another thread finished the work, do
                // not throw error to prevent reading the data from kafka again!
                log.error("Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                        paymentResponse.getOrderId());
            } catch (OrderNotFoundException e) {
                // NO-OP for OrderNotFoundException
                log.error("No order found for order id: {}", paymentResponse.getOrderId());
            }
        });

    }
}
