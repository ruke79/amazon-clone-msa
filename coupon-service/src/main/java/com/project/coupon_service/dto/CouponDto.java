package com.project.coupon_service.dto;

import com.project.coupon_service.model.Coupon;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponDto {

    private String id;

    private String name;
     
    private String startDate;
    
    private String endDate;

    private int discount;

    public static CouponDto convertToDto(Coupon coupon) {
        return CouponDto.builder()
                .id(Long.toString(coupon.getCouponId()))
                .name(coupon.getName())
                .startDate(coupon.getStartDate().toString())
                .endDate(coupon.getEndDate().toString())
                .discount(coupon.getDiscount())
                .build();

    }
    
}
