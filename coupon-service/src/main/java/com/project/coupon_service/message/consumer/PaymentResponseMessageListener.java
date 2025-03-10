package com.project.coupon_service.message.consumer;


import org.springframework.stereotype.Service;

import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.coupon_service.exception.CustomDomainException;
import com.project.coupon_service.model.Coupon;
import com.project.coupon_service.service.CouponService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentResponseMessageListener {

    private final CouponService couponService;

    @Transactional
    public void completeCoupon(PaymentResponse response) {

                
        Coupon coupon = couponService.updateCouponStatus(response);
        
        if (coupon == null) {
            log.error("Coupon could not be used in coupon database with name: {}", response.getCouponName());
            throw new CustomDomainException("Coupon could not be used in coupon database with name " +
            response.getCouponName());
        }
        log.info("Coupon is used in coupon database with id: {}", coupon.getCouponId());
        
    }

}
