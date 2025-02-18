package com.project.user-service.security.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String username;
    private String email;
    private String password;
};