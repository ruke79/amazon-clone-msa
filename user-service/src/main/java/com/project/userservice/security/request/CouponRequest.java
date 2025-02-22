package com.project.userservice.security.request;

import com.project.userservice.dto.CouponDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CouponRequest {

    private CouponDto coupon;
}
