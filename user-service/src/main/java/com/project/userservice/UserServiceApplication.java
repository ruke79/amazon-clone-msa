package com.project.userservice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.annotation.XmlElement.DEFAULT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@ConfigurationPropertiesScan 
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {

		

		
		SpringApplication.run(UserServiceApplication.class, args);



		
	}

	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
		return new HiddenHttpMethodFilter();
	}

}
