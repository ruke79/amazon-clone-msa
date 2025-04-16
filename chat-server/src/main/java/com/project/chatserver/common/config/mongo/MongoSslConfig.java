package com.project.chatserver.common.config.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.TransportSettings;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "com.project.chatserver.repository")
public class MongoSslConfig {
    private final MongoProperties mongoProperties;

    @Value("${spring.data.mongodb.cacert}")
    private String certificateDecoded; //your CA Certifcate decoded (starts with BEGIN CERTIFICATE)

    @Value("${spring.data.mongodb.keystore}")
    private String keystoreFile; //your CA Certifcate decoded (starts with BEGIN CERTIFICATE)

    @Value("${spring.data.mongodb.keystorePassword}")
    private String keystorePassword; //your CA Certifcate decoded (starts with BEGIN CERTIFICATE)

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoClientOptions());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), mongoProperties.getName());
    }

    @Bean
    public MongoClientSettings mongoClientOptions() {
        MongoClientSettings.Builder mongoClientOptions = MongoClientSettings.builder().applyConnectionString(
            new ConnectionString(mongoProperties.getClient()));
        try {
            
                        
            //InputStream inputStream = new ByteArrayInputStream(certificateDecoded.getBytes(StandardCharsets.UTF_8));
            FileInputStream inputStream = new FileInputStream(certificateDecoded);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) certificateFactory.generateCertificate(inputStream);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            
            //keyStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray()); // You don't need the KeyStore instance to come from a file.
            keyStore.load(null); // You don't need the KeyStore instance to come from a file.
            keyStore.setCertificateEntry("cacert", caCert);

            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            mongoClientOptions.applyToSslSettings(builder -> {
                builder.enabled(true);
                //builder.invalidHostNameAllowed(true);                
                builder.context(sslContext);
            })
            .applyToConnectionPoolSettings(builder -> builder.maxConnectionIdleTime(10L, TimeUnit.MINUTES))
             .readConcern(ReadConcern.DEFAULT)
        .writeConcern(WriteConcern.MAJORITY)
        .readPreference(ReadPreference.primary())
            //.applyToSocketSettings(socketSettings -> socketSettings
            //.connectTimeout(1, TimeUnit.MINUTES)
            //.readTimeout(1, TimeUnit.MINUTES))
            .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return mongoClientOptions.build();
    }
   

}
