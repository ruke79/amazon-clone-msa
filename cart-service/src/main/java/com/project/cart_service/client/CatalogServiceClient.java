package com.project.cart_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.cart_service.config.FeignConfig;
import com.project.common.dto.ProductDto;


/**
 * 마이크로서비스 간의 호출을 위한 feignclient
 */
@FeignClient(name = "catalog-service")
public interface CatalogServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/{productId}")
    public ResponseEntity<ProductDto> getProductInfo(@PathVariable("productId") String productId);

    @GetMapping("/api/cart/{product_id}")
    public ResponseEntity<?> getCartProductInfo(@PathVariable("product_id") String product_id,
            @RequestParam("style") int style, @RequestParam("size") int size);

    @GetMapping("/api/color/{colorId}")
    public ResponseEntity<?> getColorInfo(@PathVariable("colorId") String colorId);
}
