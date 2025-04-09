package com.project.userservice.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.project.userservice.constants.AppRole;
import com.project.userservice.dto.CustomOAuth2User;
import com.project.userservice.dto.UserProfileDto;
import com.project.userservice.model.Role;
import com.project.userservice.model.User;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.request.SignupRequest;
import com.project.userservice.security.response.GoogleResponse;
import com.project.userservice.security.response.NaverResponse;
import com.project.userservice.security.response.OAuth2Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepository userRepository;

     

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        String  username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        User existData = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found with name: " + username));;

        String role = AppRole.ROLE_USER.getRole();
        if (existData == null) {  
            User user = new User();
            user.setUsername(username);
            user.setEmail(oAuth2Response.getEmail());            
            user.setRole(new Role(AppRole.ROLE_USER));

            userRepository.save(user);
        }
        else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUsername(username);
           
            userRepository.save(existData);
                       
        }
        return new CustomOAuth2User(oAuth2Response, role);
    }            
}
