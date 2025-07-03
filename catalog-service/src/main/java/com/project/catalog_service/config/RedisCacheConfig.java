package com.project.catalog_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.project.common.dto.ProductDto;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String  password;

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean useSsl;


    @Value("${spring.data.redis.ssl.cacert}")
    private String cacertFileLocation;

    @Value("${spring.data.redis.ssl.key}")
    private String keyFileLocation;

    @Value("${spring.data.redis.ssl.cert}")
    private String certFileLocation;

    @Value("${spring.data.redis.ssl.storePassword}")
    private String storePassword;

    private final ResourceLoader resourceLoader;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws IOException {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setPassword(RedisPassword.of(password));

        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder =
        LettuceClientConfiguration.builder();

    if (useSsl){
      SslOptions sslOptions = SslOptions.builder()
          .trustManager(resourceLoader.getResource("file:" + cacertFileLocation).getFile())
          .truststore(resourceLoader.getResource("file:" + certFileLocation).getFile(), storePassword)
          .keystore(resourceLoader.getResource("file:" + keyFileLocation).getFile(), storePassword.toCharArray())          
          .build();

    

      ClientOptions clientOptions = ClientOptions
          .builder()
          .sslOptions(sslOptions)
          .protocolVersion(ProtocolVersion.RESP3)
          .build();

      lettuceClientConfigurationBuilder
          .clientOptions(clientOptions)
          .useSsl().disablePeerVerification();
    }

    LettuceClientConfiguration lettuceClientConfiguration = lettuceClientConfigurationBuilder.build();

    
    return new LettuceConnectionFactory(redisConfig, lettuceClientConfiguration);

        
    }

   

    @Bean
    public RedisTemplate<String, Object> redisTemplate() throws IOException {
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
    public RedisCacheManager redisCacheManager() throws IOException {

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
