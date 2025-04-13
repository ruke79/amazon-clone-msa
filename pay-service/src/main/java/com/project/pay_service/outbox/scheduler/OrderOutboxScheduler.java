package com.project.pay_service.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.outbox.OutboxScheduler;
import com.project.common.outbox.OutboxStatus;
import com.project.pay_service.outbox.OrderOutboxHelper;
import com.project.pay_service.outbox.model.OrderOutboxEvent;
import com.project.pay_service.ports.output.message.publisher.PaymentResponseKafkaPublisher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxScheduler implements OutboxScheduler {


    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseKafkaPublisher paymentResponseMessagePublisher;


    @Override
    @Transactional
    @Scheduled(fixedDelayString = "10", //"${pay-service.outbox-scheduler-fixed-rate}",
                initialDelayString = "3" )//"${pay-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        MDC.put("DONOTLOG", "true");

        Optional<List<OrderOutboxEvent>> outboxEvents = 
        orderOutboxHelper.getOrderOutboxEventByOutboxStatus(OutboxStatus.STARTED);


        if (outboxEvents.isPresent() && outboxEvents.get().size() > 0) {
            List<OrderOutboxEvent> outboxList = outboxEvents.get();

            // log.info("Received {} PaymentOutboxEvent with ids: {}, sending to message bus!",
            // outboxList.size(),
            // outboxList.stream().map(outboxMessage ->
            //                outboxMessage.getId().toString()).collect(Collectors.joining(",")));

            outboxList.forEach(outbox ->
            paymentResponseMessagePublisher.publish(outbox, this::updateOutboxStatus));
        }        

        MDC.remove("DONOTLOG");

    }

    private void updateOutboxStatus(OrderOutboxEvent outboxEvent, OutboxStatus outboxStatus) {
        outboxEvent.setOutboxStatus(outboxStatus);
        orderOutboxHelper.save(outboxEvent);
        //log.info("OutboxEvent is updated with outbox status: {}", outboxStatus.name());
    }

}