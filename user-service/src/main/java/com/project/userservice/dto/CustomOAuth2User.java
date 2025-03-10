package com.project.userservice.dto;

import com.project.userservice.dto.UserProfileDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;



public class CustomOAuth2User implements OAuth2User {

    private final UserProfileDto userDto;

    public CustomOAuth2User(UserProfileDto userDto) {

        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        userDto.getRoles().forEach(role-> collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return role;
            }
        })
        );

        return collection;
    }

    @Override
    public String getName() {

        return userDto.getEmail();
    }

    public String getUsername() {

        return userDto.getUsername();
    }
}
