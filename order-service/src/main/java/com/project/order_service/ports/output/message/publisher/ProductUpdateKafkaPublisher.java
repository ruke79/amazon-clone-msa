package com.project.order_service.ports.output.message.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.message.dto.request.ProductUpdateRequest;
import com.project.common.outbox.OutboxStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductUpdateKafkaPublisher {

    private final KafkaTemplate<String, ProductUpdateRequest> kafkaTemplate;

    public void publish(ProductUpdateRequest message) {

                
            try {
                CompletableFuture<SendResult<String, ProductUpdateRequest>> kafkaResultFuture = kafkaTemplate.send("product-update", message);
                
                kafkaResultFuture.whenComplete((result, ex) -> {
                    if (ex == null) {                   
        
                    }
                    else {
                        log.error (ex.getMessage());
                    }
                });                   
    
            } catch(KafkaException e) {
    
                log.error("Error while sending ProductUpdateRequest" +
                " to kafka with error: {}", e.getMessage());                

            }
        
    }
}
