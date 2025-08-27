package com.project.chatserver.common.config.redis;

import org.redisson.api.RedissonClient;

import java.io.File;
import java.net.URL;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean useSsl;

    @Value("${spring.data.redis.ssl.cert}")
    private String truststoreFileLocation;

    @Value("${spring.data.redis.ssl.key}")
    private String keystoreFileLocation;
    
    @Value("${spring.data.redis.ssl.storePassword}")
    private String storePassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("rediss://mywebserv.site:6379");
        
        if (useSsl) {
             try {
                // File 객체를 URL 객체로 변환
                URL truststoreUrl = new File(truststoreFileLocation).toURI().toURL();
                URL keystoreUrl = new File(keystoreFileLocation).toURI().toURL();

                config.useSingleServer()
                    .setSslTruststore(truststoreUrl)
                    .setSslKeystore(keystoreUrl);
                
                if (storePassword != null && !storePassword.isEmpty()) {
                    config.useSingleServer()
                        .setSslTruststorePassword(storePassword)
                        .setSslKeystorePassword(storePassword);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to load SSL certificates for Redis.", e);
            }
        }
        return Redisson.create(config);
    }
}
