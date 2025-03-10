package com.project.coupon_service.message.consumer;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;


import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.coupon_service.dto.request.CouponRequest;
import com.project.coupon_service.exception.CustomDomainException;
import com.project.coupon_service.model.Coupon;
import com.project.coupon_service.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageListener {

    private final CouponService couponService;

    
    void userCreated(UserCreatedRequest createdUser) {

         LocalDateTime now = LocalDateTime.now();
         CouponRequest req = new CouponRequest("30% discount coupon", now, now.plusDays(31), 30, Long.toString(createdUser.getUserId()));
         Coupon coupon = couponService.create(req);
        
        if (coupon == null) {
            log.error("Coupon could not be created in coupon database with id: {}", createdUser.getUserId());
            throw new CustomDomainException("Coupon could not be created in coupon database with id " +
            createdUser.getUserId());
        }
        log.debug("Coupon is created in coupon database with id: {}", coupon.getCouponId());


    }
}
