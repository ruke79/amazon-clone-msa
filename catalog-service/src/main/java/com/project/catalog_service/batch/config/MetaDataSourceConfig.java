package com.project.catalog_service.batch.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class MetaDataSourceConfig {

    @Bean(name = "metaDataSourceProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource-meta")
    public DataSourceProperties metaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "metaDataSource")
    @Primary
    public DataSource metaDataSource() {
        return metaDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "metaTransactionManager")
    @Primary
    public PlatformTransactionManager metaTransactionManager() {
        return new DataSourceTransactionManager(metaDataSource());
    }
}