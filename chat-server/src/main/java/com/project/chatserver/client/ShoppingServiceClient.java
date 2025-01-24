package com.project.chatserver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 마이크로서비스 간의 호출을 위한 feignclient
 */
@FeignClient(name="shopping-service")
public interface ShoppingServiceClient {

   //@GetMapping("api/auth/user/email")
   // ResponseEntity<SharedUserDTO> findByEmail(@RequestParam String email);
}
