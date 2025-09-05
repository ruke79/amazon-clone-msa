package com.project.gatewayserver.filter;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import com.project.common.constants.TokenStatus;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
//@Component  --> 이 필터는 application.yml에서 수동으로 등록
public class JwtAuthorizationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthorizationGatewayFilterFactory.Config> {
    
    //Environment env;

    
    public static class Config {
                
    }

    @Value("${spring.app.jwtSecret}")
    private String secretKey;

    public JwtAuthorizationGatewayFilterFactory() {
        super(Config.class);
        //this.env = env;
    }

    @Override
    public GatewayFilter apply(Config config) {

        //return new OrderedGatewayFilter(
        return (exchange, chain) -> {
                        
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();                
              // WebSocket 관련 경로를 필터 검사에서 제외
            if (path.startsWith("/chat-service/chat/")) {
                return chain.filter(exchange);
            }
            

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return OnError(exchange, "로그인이 필요한 서비스입니다.", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            try {           

                isJwtTokenExpired(jwt);                
                
            } catch (ExpiredJwtException e) {                
                return OnError(exchange, "access token expired", HttpStatus.UNAUTHORIZED);
            }



            TokenStatus tokenStatus = validateJwtToken(jwt); 

            if (tokenStatus == TokenStatus.VALID) {

                // JWT에서 user id 추출
                String userId = extractUserIdFromJwt(jwt);

                ServerHttpRequest newRequest = request.mutate()
                        .header("User-Id", userId)
                        .build();

                // 수정된 요청으로 교환 객체 업데이트
                return chain.filter(exchange.mutate().request(newRequest).build());
            }
            
            return OnError(exchange, "invalid access token", HttpStatus.UNAUTHORIZED);
        };        
        //, Ordered.LOWEST_PRECEDENCE);
    }

    private SecretKey key() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    private String extractUserIdFromJwt(String token) {
        try {

            return Jwts.parser()
                    .verifyWith(key())
                    .build().parseSignedClaims(token)
                    .getPayload().getSubject();

        } catch (Exception e) {
            log.error("Failed to extract id from JWT", e);
            return null;
        }
    }

    // private boolean isJwtValid(String jwt) {
    // boolean returnValue = true;
    // String id = null;

    // try {
    // //복호화
    // Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
    // JWTVerifier verifier = JWT.require(algorithm).build();

    // DecodedJWT decodedJWT = verifier.verify(jwt);
    // id = decodedJWT.getSubject();
    // } catch (Exception e) {
    // returnValue = false;
    // }
    // if (id == null || id.isEmpty()) {
    // returnValue = false;
    // }

    // return returnValue;
    // }

    private boolean isJwtTokenExpired(String token) {

        return Jwts.parser()
        .verifyWith(key())
        .build().parseSignedClaims(token)
        .getPayload().getExpiration().before(new Date());
     }

    
     public TokenStatus validateJwtToken(String authToken) {
    try {
      Jwts.parser().verifyWith(key())
          .build().parseSignedClaims(authToken);
      return TokenStatus.VALID;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      return TokenStatus.INVALID;
    } catch (ExpiredJwtException e) {
        log.error("JWT token is expired: {}", e.getMessage());
      return TokenStatus.EXPIRED;
    } catch (UnsupportedJwtException e) {
        log.error("JWT token is unsupported: {}", e.getMessage());
      return TokenStatus.UNSUPPORTED;
    } catch (IllegalArgumentException e) {
        log.error("JWT claims string is empty: {}", e.getMessage());
      return TokenStatus.ILLEGAL_ARGS;
    }
  }
    

    // Mono, Flux => Spring WebFlux
    private Mono<Void> OnError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        // 문자열로 에러 메시지 생성
        String errorMessage = err;
        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(bytes);

        // 컨텐트 타입 설정
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);

        // 에러 로깅
        log.error(err);

        // 에러 메시지를 응답 본문에 쓰기
        return response.writeWith(Flux.just(buffer));
    }

}
