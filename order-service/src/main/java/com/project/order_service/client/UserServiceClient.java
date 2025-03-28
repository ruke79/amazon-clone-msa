package com.project.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.order_service.config.FeignConfig;


/**
 * 마이크로서비스 간의 호출을 위한 feignclient
 */
@Component
@FeignClient(name="user-service", url="${feign.user-url}", configuration = FeignConfig.class)
public interface UserServiceClient {

   @GetMapping("api/auth/user/id")
   public ResponseEntity<?> findUserByEmail(@RequestParam("email") String email);

   @GetMapping("api/auth/user/name")
   public ResponseEntity<?> findUserByName(@RequestParam("userName") String userName);


   @GetMapping("/api/user/profile/address")
   ResponseEntity<?> getUserInfoWithAddresses();

   @GetMapping("/api/user/profile/select/{addressId}")
    ResponseEntity<?> selectShippingAddresses(@PathVariable("addressId") String addressId);
}
