package com.project.userservice.service;

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
import com.project.userservice.model.User;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.request.SignupRequest;
import com.project.userservice.security.response.GoogleResponse;
import com.project.userservice.security.response.NaverResponse;
import com.project.userservice.security.response.OAuth2Response;
import com.project.userservice.security.service.UserDetailsServiceImpl;


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

        String name = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        User existData = userRepository.findByName(name)
        .orElseThrow(() -> new RuntimeException("User not found with name: " + name));;

        if (existData == null) {  
            // SignupRequest request = new SignupRequest();
            // request.setEmail(oAuth2Response.getEmail());
            // request.setUsername(oAuth2Response.getName());            
            // request.setName(name);
            // Set<String> roles = new HashSet<>();
            // roles.add(AppRole.ROLE_USER.getRole());
            // request.setRole(roles);

            
            //userServiceImpl.registerNewUserAccount(request);

            UserProfileDto userDto = UserProfileDto.builder()
            .username(oAuth2Response.getName())
            .name(name)
            .email(oAuth2Response.getEmail())            
            .build();
            userDto.getRoles().add(AppRole.ROLE_USER.getRole());

            return new CustomOAuth2User(userDto);
        }
        else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUserName(oAuth2Response.getName());

            userRepository.save(existData);

            UserProfileDto userDto = new UserProfileDto();
            userDto.setUsername(oAuth2Response.getName());
            userDto.setName(existData.getName());
            userDto.getRoles().add(existData.getRole().getRoleName().getRole());

            return new CustomOAuth2User(userDto);
        }
    }            
}
