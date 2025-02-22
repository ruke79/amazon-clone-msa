package com.project.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "cart-service")
public interface CartServiceClient {

    @GetMapping("api/cart/{cartProductId}") 
    ResponseEntity<?> getProductId(@PathVariable String cartProdcutId);
}
