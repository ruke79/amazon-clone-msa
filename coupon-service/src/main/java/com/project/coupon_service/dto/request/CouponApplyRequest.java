package com.project.coupon_service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.coupon_service.dto.CouponDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CouponApplyRequest {

    BigDecimal totalPrice;

    private String couponName;     
    
    private String userEmail;
}
