package com.project.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "cart-service", url="${feign.cart-url}")
public interface CartServiceClient {

    @GetMapping("api/cart/{cartProductId}") 
    String getProductId(@PathVariable("cartProductId") String cartProdcutId);
}
