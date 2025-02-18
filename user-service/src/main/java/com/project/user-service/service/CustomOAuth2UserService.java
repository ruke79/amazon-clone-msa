package com.project.user-service.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.project.user-service.constants.AppRole;
import com.project.user-service.dto.CustomOAuth2User;
import com.project.user-service.dto.UserProfileDTO;
import com.project.user-service.model.User;
import com.project.user-service.repository.UserRepository;
import com.project.user-service.security.request.SignupRequest;
import com.project.user-service.security.response.GoogleResponse;
import com.project.user-service.security.response.NaverResponse;
import com.project.user-service.security.response.OAuth2Response;
import com.project.user-service.service.impl.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


     private final UserRepository userRepository;

     //private final UserServiceImpl  userServiceImpl;

    //  @Autowired
    //  public CustomOAuth2UserService(UserRepository userRepository, UserServiceImpl userServiceImpl) {
    //     this.userRepository = userRepository;
    //     this.userServiceImpl = userServiceImpl;
    // }

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
            SignupRequest request = new SignupRequest();
            request.setEmail(oAuth2Response.getEmail());
            request.setUsername(oAuth2Response.getName());            
            request.setName(name);
            Set<String> roles = new HashSet<>();
            roles.add(AppRole.ROLE_USER.getRole());
            request.setRole(roles);

            //userServiceImpl.registerNewUserAccount(request);

            UserProfileDTO userDTO = UserProfileDTO.builder()
            .username(oAuth2Response.getName())
            .name(name)
            .role(AppRole.ROLE_USER.getRole())
            .build();

            return new CustomOAuth2User(userDTO);
        }
        else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setUserName(oAuth2Response.getName());

            userRepository.save(existData);

            UserProfileDTO userDTO = new UserProfileDTO();
            userDTO.setUsername(oAuth2Response.getName());
            userDTO.setName(existData.getName());
            userDTO.setRole(existData.getRole().getRoleName().getRole());

            return new CustomOAuth2User(userDTO);
        }
    }            
}
