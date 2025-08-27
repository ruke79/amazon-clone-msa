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

import javax.net.ssl.KeyManagerFactory;
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
    private String caCertificatePath; //your CA Certifcate decoded (starts with BEGIN CERTIFICATE)

    @Value("${spring.data.mongodb.keystore}")
    private String keystoreFile; //your CA Certifcate decoded (starts with BEGIN CERTIFICATE)

    @Value("${spring.data.mongodb.keystorePassword}")
    private String keystorePassword; //your CA Certifcate decoded (starts with BEGIN CERTIFICATE)
    
    // @Value("${spring.data.mongodb.truststore}") // 새로운 프로퍼티로 truststore 파일 경로 지정
    // private String truststoreFile;
    // @Value("${spring.data.mongodb.truststorePassword}") // truststore 비밀번호 지정
    // private String truststorePassword;


    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), mongoProperties.getName());
    }

    @Bean
    public MongoClient mongoClient() {
        
        try {   
            
            // TrustStore 설정: truststore 파일을 직접 로드
            // KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // FileInputStream trustStoreStream = new FileInputStream(truststoreFile);
            // trustStore.load(trustStoreStream, truststorePassword.toCharArray());
            // trustStoreStream.close();
             
            // .pem CA 인증서 설정 (신뢰할 수 있는 인증서)
            FileInputStream caFileStream  = new FileInputStream(caCertificatePath);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) certificateFactory.generateCertificate(caFileStream );
            caFileStream.close();
            

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry("ca", caCert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            
             // KeyStore 설정 (클라이언트 인증서 및 키)
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream keyStoreStream = new FileInputStream(keystoreFile);
            keyStore.load(keyStoreStream, keystorePassword.toCharArray());
            keyStoreStream.close();
                       
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keystorePassword.toCharArray());
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);  // 이 부분이 중요

            // KeyManagerFactory와 TrustManagerFactory 모두 사용하여 상호 TLS 설정
            //sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // MongoClientSettings에 SSL Context 설정
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoProperties.getClient()))
                .applyToSslSettings(builder -> builder.enabled(true).context(sslContext))
                .applyToConnectionPoolSettings(builder -> builder.maxConnectionIdleTime(10L, TimeUnit.SECONDS))
                .readConcern(ReadConcern.DEFAULT)
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .build();
            
            return MongoClients.create(settings);

            
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure MongoDB with SSL.", e);
        }        
    }
   

}
