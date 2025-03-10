package com.project.coupon_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.constants.StatusMessages;
import com.project.common.dto.ProductQADto;
import com.project.common.response.MessageResponse;
import com.project.coupon_service.dto.CouponDto;
import com.project.coupon_service.dto.request.CouponRequest;
import com.project.coupon_service.dto.response.CouponResponse;
import com.project.coupon_service.dto.request.CouponApplyRequest;
import com.project.coupon_service.model.Coupon;
import com.project.coupon_service.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("api/coupon")
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;


    @PostMapping("/add")
    ResponseEntity<?> addCoupon(@RequestBody CouponRequest request) {

        try {
            
            Coupon newCoupon = couponService.create(request);

            if (null != newCoupon) {

                return new ResponseEntity<>(CouponDto.convertToDto(newCoupon), HttpStatus.OK);
            }
            else 
                return new ResponseEntity<>(new MessageResponse(StatusMessages.COUPON_IS_EXISTED), HttpStatus.OK);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to create a coupon."));
        }
    }

    @PostMapping("/applycoupon")
    ResponseEntity<?> applyCoupon(@RequestBody CouponApplyRequest request) { 

                    
         CouponResponse response = couponService.applyCoupon(request);
         return new ResponseEntity<>(response, HttpStatus.OK);
                        
        
    }

    @GetMapping("/coupons") 
    ResponseEntity<?> getNotUsedCoupons(@RequestParam(name="email") String userId) {

        log.info("email : {}", userId);

        try {
            List<CouponDto> coupons = couponService.getCouponsNotUsed(userId);

            List<String> result = coupons.stream().map(coupon -> {
                return coupon.getName();
            }).collect(Collectors.toList());

            return new ResponseEntity<>(result, HttpStatus.OK);            

        } catch (RuntimeException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                     .body(new MessageResponse("Faield to get not used coupons."));
            
        }
        
    }

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteCoupon(@RequestParam String id) {

        try {
            couponService.delete(id);

            return new ResponseEntity<>(new MessageResponse("succeeded to delete a coupon"), HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Faield to create a coupon."));
        }
    }

    @PutMapping("/update")
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
