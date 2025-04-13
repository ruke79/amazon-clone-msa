package com.project.order_service.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.outbox.OutboxScheduler;
import com.project.common.outbox.OutboxStatus;
import com.project.order_service.outbox.PaymentOutboxHelper;
import com.project.order_service.outbox.model.PaymentOutboxEvent;
import com.project.order_service.ports.output.message.publisher.PaymentRequestKafkaPublisher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutboxScheduler implements OutboxScheduler {


    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestKafkaPublisher paymentRequestMessagePublisher;

    
    @Override
    @Transactional
    @Scheduled(fixedDelayString = "10", //"${order-service.outbox-scheduler-fixed-rate}",
                initialDelayString = "3" )//"${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        MDC.put("DONOTLOG", "true");
        
        Optional<List<PaymentOutboxEvent>> outboxEvents = 
        paymentOutboxHelper.getPaymentOutboxEventByOutboxStatus(OutboxStatus.STARTED);


        if (outboxEvents.isPresent() && outboxEvents.get().size() > 0) {
            List<PaymentOutboxEvent> outboxList = outboxEvents.get();

            
            // log.info("Received {} PaymentOutboxEvent with ids: {}, sending to message bus!",
            // outboxList.size(),
            // outboxList.stream().map(outboxMessage ->
            //                outboxMessage.getId().toString()).collect(Collectors.joining(",")));

            outboxList.forEach(outbox ->
            paymentRequestMessagePublisher.publish(outbox, this::updateOutboxStatus));
        }        

        MDC.remove("DONOTLOG");    

    }

    private void updateOutboxStatus(PaymentOutboxEvent outboxEvent, OutboxStatus outboxStatus) {
        outboxEvent.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(outboxEvent);
        log.info("PaymentOutboxEvent is updated with outbox status: {}", outboxStatus.name());
    }

}