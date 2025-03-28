package com.project.chatserver.common.config.interceptor;


import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.project.chatserver.common.config.interceptor.StompHandler;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker //websocker 활상화하고 메세지 브로커 사용가능
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    //private final StompHandler stompHandler;
    
    /**
     * STOMP 엔드포인트를 등록하는 메서드
     * Stomp 연결을 프론트에서 시도할때 요청을 보낼 엔드포인트를 지정하는 부분
     * param StompEndpointRegistry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") //stomp endpoint 지정
                .setAllowedOriginPatterns("*")
                .withSockJS();

    }
    /**
     * 메시지 브로커를 구성하는 메서드
     * 채팅방번호로 구독하는 채팅방을 구분
     * param MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // /subscribe/{chatNo}로 주제 구독 가능
        registry.setApplicationDestinationPrefixes("/pub");// /publish/message로 메시지 전송 컨트롤러 라우팅 가능

    }

    // prevent circular referenece
    @Bean 
    public StompHandler myChannelInterceptor() {
        return new StompHandler();
    }

    /**
     * 클라이언트 인바운드 채널을 구성하는 메서드
     * JWT 인증 절차가 채팅에서도 적용
     * param ChannelRegistration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // stompHandler를 인터셉터로 등록하여 STOMP 메시지 핸들링을 수행
        registration.interceptors(myChannelInterceptor());
    }
    /**
     * STOMP에서 64KB 이상의 데이터 전송을 못하는 문제 해결
     * param WebSocketTransportRegistration
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(160 * 64 * 1024);
        registry.setSendTimeLimit(100 * 10000);
        registry.setSendBufferSizeLimit(3 * 512 * 1024);

    }
}
