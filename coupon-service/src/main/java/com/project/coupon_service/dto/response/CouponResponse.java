package com.project.coupon_service.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {

    private BigDecimal totalAfterDiscount;
    private int discount;
    private String message;    
}
