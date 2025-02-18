package com.project.user-service.security.request;

import com.project.user-service.dto.CouponDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CouponRequest {

    private CouponDTO coupon;
}
