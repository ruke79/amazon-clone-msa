package com.project.gatewayserver.fallback;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

@RestController
@Slf4j
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<ResponseEntity<String>> fallback(ServerWebExchange exchange) {
        // CircuitBreaker에서 발생한 예외를 가져옵니다.
        Throwable exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        String errorMessage = "내부 서버 오류가 발생했습니다. 관리자에게 문의해 주세요.";
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        // 예외 종류에 따라 로그와 응답 메시지를 다르게 처리합니다.
        if (exception != null) {
            log.warn("Fallback triggered. Exception: {}", exception.getMessage());
            
            if (exception instanceof NotFoundException) {
                errorMessage = "요청한 서비스를 찾을 수 없습니다.";
                status = HttpStatus.NOT_FOUND;
            } else if (exception instanceof TimeoutException) {
                errorMessage = "서비스 요청 시간이 초과되었습니다. 잠시 후 다시 시도해 주세요.";
                status = HttpStatus.GATEWAY_TIMEOUT;
            } else if (exception instanceof CallNotPermittedException) {
                errorMessage = "서비스가 현재 사용 불가능합니다. 잠시 후 다시 시도해 주세요.";
                status = HttpStatus.SERVICE_UNAVAILABLE;
            }
        } else {
            log.warn("Fallback triggered with no specific exception.");
        }

        return Mono.just(new ResponseEntity<>(errorMessage, status));
    }
}