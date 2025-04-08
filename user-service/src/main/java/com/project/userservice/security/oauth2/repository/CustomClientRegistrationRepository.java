package com.project.userservice.security.oauth2.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import com.project.userservice.security.oauth2.SocialClientRegistration;

@Configuration
public class CustomClientRegistrationRepository {

      private final SocialClientRegistration socialClientRegistration;

    public CustomClientRegistrationRepository(SocialClientRegistration socialClientRegistration) {

        this.socialClientRegistration = socialClientRegistration;
    }

    public ClientRegistrationRepository clientRegistrationRepository() {

        return new InMemoryClientRegistrationRepository(socialClientRegistration.naverClientRegistration(), socialClientRegistration.googleClientRegistration());
    }

}
