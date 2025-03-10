package com.project.userservice.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedRequest implements Serializable{

    private Long userId;
    private String username; 
    private String nickname; 
    private String email;   
}
