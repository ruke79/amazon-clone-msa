package com.project.chatserver.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * 마이크로서비스 간의 호출을 위한 feignclient
 */
@FeignClient(name="shopping-service")
public interface ShoppingServiceClient {

    ///api/auth/users
}
