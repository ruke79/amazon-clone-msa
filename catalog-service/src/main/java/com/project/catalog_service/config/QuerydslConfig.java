package com.project.catalog_service.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
// @EnableJpaRepositories(
//     basePackages = "com.project.catalog_service.repository",
//     entityManagerFactoryRef = "entityManagerFactory",
//     transactionManagerRef = "transactionManager"
// )
public class QuerydslConfig {

    @PersistenceContext(unitName = "catalogPersistenceUnit")
    private EntityManager entityManager;

    /**
     * JPAQueryFactory 빈을 등록합니다.
     * 이 빈은 Querydsl을 사용하여 동적 쿼리를 생성할 때 사용됩니다.
     *
     * @return JPAQueryFactory 빈
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}