
package com.project.userservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String  password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisConfiguration);
    }

   @Bean
   @Primary
   public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
       RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
       redisTemplate.setKeySerializer(new StringRedisSerializer());
       //redisTemplate.setValueSerializer(new StringRedisSerializer());
       redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
       redisTemplate.setConnectionFactory(redisConnectionFactory());
       return redisTemplate;
   }

    @Bean(name = "redisObjectMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());   // Java 8 date/time type `java.time.LocalDateTime` not supported by default
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
  

   @Bean
   public StringRedisTemplate stringRedisTemplate() {
       final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
       stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
       stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
       stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
       return stringRedisTemplate;
   }
}


