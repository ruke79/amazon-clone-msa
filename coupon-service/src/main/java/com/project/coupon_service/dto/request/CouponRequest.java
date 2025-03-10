package com.project.coupon_service.dto.request;

import java.time.LocalDateTime;

import com.project.coupon_service.dto.CouponDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CouponRequest {

    
    private String name;
     
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;

    private int discount;

    private String userId;
}
