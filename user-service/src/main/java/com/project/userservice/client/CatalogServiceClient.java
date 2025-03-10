package com.project.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.common.dto.ProductDto;






/**
 * 마이크로서비스 간의 호출을 위한 feignclient
 */
@FeignClient(name="catalog-service")
public interface CatalogServiceClient {

   @RequestMapping(method=RequestMethod.PUT, value="/api/product/rating")
   public ResponseEntity<ProductDto> updateRating(@RequestParam Long id, @RequestParam float rating);

   @RequestMapping(method=RequestMethod.GET, value="/api/{productId}")
   public ResponseEntity<ProductDto> getProductInfo(@PathVariable("productId") String productId);
}
