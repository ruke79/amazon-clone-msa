package com.project.chatserver.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
@Slf4j
@Component
public class TokenHandler {
    @Value("${spring.app.jwtSecret}")
    private String secretKey;
    private final Algorithm algorithm;

    public TokenHandler() {
        this.algorithm = Algorithm.HMAC256("mySecretKey123912738aopsgjnspkmndfsopkvajoirjg94gf2opfng2moknm".getBytes());
    }

    public String getUid(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    public boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception e) {
            log.info("토큰 만료 = {}", token);
            return false;
        }
    }
}