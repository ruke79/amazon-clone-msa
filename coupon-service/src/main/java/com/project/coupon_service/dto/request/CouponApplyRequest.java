package com.project.coupon_service.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.coupon_service.dto.CouponDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CouponApplyRequest {

    BigDecimal totalPrice;

    private String couponName;     
    
    private String userEmail;
}
