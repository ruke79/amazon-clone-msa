package com.project.catalog_service.batch.config;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

import java.util.HashMap;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.project.catalog_service.repository",
        entityManagerFactoryRef = "dataEntityManager",
        transactionManagerRef = "batchTransactionManager"
)
public class DataSourceConfig {

    

  // Application Data(JPA)를 위한 DataSourceProperties 설정 추가
    @Bean(name = "dataDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource-data")
    public DataSourceProperties dataDataSourceProperties() {
        return new DataSourceProperties();
    }

    // Application Data(JPA)를 위한 DataSource 설정
    @Bean(name = "dataDataSource")
    public DataSource dataDataSource() {
        return dataDataSourceProperties().initializeDataSourceBuilder().build();
    }

     @Bean
    public LocalContainerEntityManagerFactoryBean dataEntityManager() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataDataSource());
        em.setPackagesToScan(new String[]{"com.project.catalog_service.model"});
        em. setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", "true");
        em.setJpaPropertyMap(properties);

        return em;
    }

   // Batch Transaction Manager를 JpaTransactionManager로 변경
    @Bean(name = "batchTransactionManager")
    public PlatformTransactionManager batchTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
    
}

