package com.project.chatserver.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import  jakarta.persistence.EntityManager;
import  jakarta.persistence.PersistenceContext;

@Configuration
public class QueryDslConfig {
    @PersistenceContext
    private EntityManager em;
 
    @Bean
    public JPAQueryFactory init() {
        return new JPAQueryFactory(em);
    }
}