package com.project.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedDTO implements Serializable{

    private Long userId;
    private String username; 
    private String nickname; 
    private String email;   
}
