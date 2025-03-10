package com.project.catalog_service.message.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.project.common.message.dto.request.ProductUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductUpdateKafkaListener {

    private final ProductUpdateMessageListener productUpdateMessageListener;

    @KafkaListener(id="product", topics="product-update")
    void receive(List<ProductUpdateRequest> messages) {

        messages.forEach(message->productUpdateMessageListener.productUpdated(message));
    }

}
