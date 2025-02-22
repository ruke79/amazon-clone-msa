package com.project.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUserDto {

    private String userId;
    private String username; // 실명
    private String nickname; // 별명
    private String email;
    private String image;
}
