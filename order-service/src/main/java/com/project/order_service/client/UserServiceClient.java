package com.project.catalog_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.catalog_service.config.FeignConfig;


/**
 * 마이크로서비스 간의 호출을 위한 feignclient
 */
@FeignClient(name="user-service", configuration = FeignConfig.class)
public interface UserServiceClient {

   
}
