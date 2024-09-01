package com.project.backend;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.annotation.XmlElement.DEFAULT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@ConfigurationPropertiesScan 
public class ChatApplication {

	public static void main(String[] args) {

		//TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

		SpringApplication.run(ChatApplication.class, args);

		//LocalDateTime now = LocalDateTime.now();//LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
        //System.out.println("시간 " + now);
	}

	// @PostConstruct
	// void onCounstruct() {
	// 	log.info("OnCounstrct");
		
	// 	TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	// }

}
