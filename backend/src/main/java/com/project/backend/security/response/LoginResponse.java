package com.project.backend.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class LoginResponse {
    //private String jwtToken;
    
    private String username;   
    private String email; 
    private List<String> roles;

    // public LoginResponse(String username, List<String> roles, String jwtToken) {
    //     this.username = username;        
    //     this.roles = roles;
    //     this.jwtToken = jwtToken;
    // }

}