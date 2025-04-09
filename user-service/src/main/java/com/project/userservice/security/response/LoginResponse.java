package com.project.userservice.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponse {
    
    private String email;       
    private List<String> roles;
    private String message;
    
}