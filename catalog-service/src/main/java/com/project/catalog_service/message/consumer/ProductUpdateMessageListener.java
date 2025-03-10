package com.project.catalog_service.message.consumer;

import org.springframework.stereotype.Service;

import com.project.catalog_service.model.ProductSize;
import com.project.catalog_service.service.ProductService;
import com.project.common.message.dto.request.ProductUpdateRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductUpdateMessageListener {

    private final ProductService productService;

    @Transactional
    void productUpdated(ProductUpdateRequest request) {


        ProductSize productSize = productService.productUpdated(request);

        if (productSize == null) {
            log.error("Product could not be updated");            
            
        }
        log.info("Product is updated");
    }

}
