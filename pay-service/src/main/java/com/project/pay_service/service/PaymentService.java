package com.project.pay_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.common.constants.PaymentStatus;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.common.outbox.OutboxStatus;
import com.project.pay_service.model.Payment;
import com.project.pay_service.outbox.OrderOutboxHelper;
import com.project.pay_service.outbox.model.OrderOutboxEvent;
import com.project.pay_service.ports.output.message.publisher.PaymentResponseKafkaPublisher;
import com.project.pay_service.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseKafkaPublisher paymentResponseKafkaPublisher;

    public void persistPayment(PaymentRequest request) {

        
        Payment pay = Payment.builder()                
                .customerId(request.getCustomerId())
                .orderId(request.getOrderId())            
                .trackingId(request.getTrackingId())
                .paypalOrderId(request.getPaypalOrderId())                
                .amounts(request.getAmounts())
                .paymentType(request.getPaymentType())
                .paymentStatus(request.getPaymentStatus())
                .paymentCreatedAt(request.getCreatedAt())                
                .build();
        
        pay = paymentRepository.save(pay);



        PaymentResponse response = PaymentResponse.builder()
        .customerId(pay.getCustomerId())
        .orderId(pay.getOrderId())
        .amounts(pay.getAmounts())
        .paymentStatus(pay.getPaymentStatus())
        .build();
        orderOutboxHelper.saveOrderOutbox(response, OutboxStatus.STARTED);

    }

    public boolean publishIfOutboxEventProcessedForPayment(String aggregateId, OutboxStatus outboxStatus) {

        OrderOutboxEvent orderOutboxEvent = getCompletedOrderOutboxEvent(aggregateId, outboxStatus);

        if (null != orderOutboxEvent) {

            paymentResponseKafkaPublisher.publish(orderOutboxEvent, orderOutboxHelper::updateOutboxStatus);

            return true;
        }

        return false;
    }


    private OrderOutboxEvent getCompletedOrderOutboxEvent(String aggregateId, OutboxStatus outboxStatus) {
        Optional<OrderOutboxEvent> outboxEvent = orderOutboxHelper.getCompletedOrderOutboxEvent(aggregateId, outboxStatus);

        if (outboxEvent.isPresent()) {
            return outboxEvent.get();
        }

        return null;

    }

}
