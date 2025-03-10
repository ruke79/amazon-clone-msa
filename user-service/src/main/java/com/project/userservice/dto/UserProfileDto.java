package com.project.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.project.userservice.model.Role;
import com.project.userservice.model.User;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String userId;
    private String username; // 실명
    private String name; // 별명
    private String email;    
    private boolean isAccountNonLocked;
    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;    
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private String twoFactorSecret;
    private boolean isTwoFactorEnabled;
    private String signUpMethod;
    private List<String> roles;
    private String image;
    private String defaultPaymentMethod;
    private List<AddressDto> addresses;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static  UserProfileDto convertToDto(User user, List<String> roles) {

        UserProfileDto dto = UserProfileDto.builder()
        .userId(Long.toString(user.getUserId()))
        .username(user.getUserName())
        .name(user.getName())
        .email(user.getEmail())
        .image(user.getImage())
        .isAccountNonLocked(user.isAccountNonLocked())
        .isAccountNonExpired(user.isAccountNonExpired())
        .isCredentialsNonExpired(user.isCredentialsNonExpired())
        .isEnabled(user.isEnabled())
        .credentialsExpiryDate(user.getCredentialsExpiryDate())
        .accountExpiryDate(user.getAccountExpiryDate())
        .isTwoFactorEnabled(user.isTwoFactorEnabled())
        .defaultPaymentMethod(user.getDefaultPaymentMethod())
        .signUpMethod(user.getSignUpMethod())        
        .roles(roles)
        .createdDate(user.getCreatedAt())
        .updatedDate(user.getUpdatedAt())
        .build();
        

        return dto;
    }
}
