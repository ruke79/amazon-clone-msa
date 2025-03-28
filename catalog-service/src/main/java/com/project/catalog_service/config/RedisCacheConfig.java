package com.project.catalog_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.project.common.dto.ProductDto;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(RedisPassword.of(password));

        return new LettuceConnectionFactory(redisConfig);
    }

   

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());        
        //redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());


        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {

        RedisCacheConfiguration cacheConfig = redisCacheConfiguration(Duration.ofMinutes(10)).disableCachingNullValues();
        
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory())
            .withCacheConfiguration("products_cache", redisCacheConfiguration(Duration.ofMinutes(60)))
            .withCacheConfiguration("product_cache", redisCacheConfiguration(Duration.ofMinutes(60)))
            .withCacheConfiguration("search_cache", redisCacheConfiguration(Duration.ofMinutes(30            )))
            .cacheDefaults(cacheConfig).build();
    }

    private RedisCacheConfiguration redisCacheConfiguration(Duration duration) {
    return RedisCacheConfiguration
        .defaultCacheConfig()
        .entryTtl(duration)
        .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }
}
