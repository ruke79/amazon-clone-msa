package com.project.coupon_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.coupon_service.dto.CouponDto;
import com.project.coupon_service.dto.request.CouponRequest;
import com.project.coupon_service.dto.response.MessageResponse;
import com.project.coupon_service.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;


      @PostMapping("/coupon")
    ResponseEntity<?> addCoupon(@RequestBody CouponRequest request) {

        try {
            if(couponService.create(request)) {

                return new ResponseEntity<>(couponService.getCoupons(), HttpStatus.OK);
            }
            else 
                return new ResponseEntity<>("Coupon not found", HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to create a coupon."));
        }
    }

    @DeleteMapping("/coupon/delete")
    ResponseEntity<?> deleteCoupon(@RequestParam String id) {

        try {
            couponService.delete(id);

            return new ResponseEntity<>(new MessageResponse("succeeded to delete a coupon"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to create a coupon."));
        }
    }

    @PostMapping("/coupon/update")
    ResponseEntity<?> updateCoupon(@RequestBody CouponRequest request) {

        try {
            CouponDto result = couponService.update(request);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to update a coupon."));
        }
    }


}
