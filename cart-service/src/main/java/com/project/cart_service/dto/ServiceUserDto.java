package com.project.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUserDto {

    private String userId;
    private String username; // 실명
    private String nickname; // 별명
    private String email;
    private String image;
}
