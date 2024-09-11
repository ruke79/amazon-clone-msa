package com.project.backend.service;

import org.springframework.stereotype.Service;

import com.project.backend.dto.CouponDTO;
import com.project.backend.model.Coupon;
import com.project.backend.repository.CouponRepository;
import com.project.backend.security.request.CouponRequest;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public boolean delete(String id) {

        Coupon coupon= couponRepository.findById(Long.parseLong(id))
        .orElseThrow(()->new RuntimeException("Coupon not found. "));

        if (null != coupon) {
            couponRepository.delete(coupon);
            return true;
        }

        return false;
    }

    public CouponDTO update(CouponRequest request) {

        Long id =Long.parseLong(request.getCoupon().getId());
        Coupon coupon= couponRepository.findById(id)
        .orElseThrow(()->new RuntimeException("Coupon not found. "));

        if (null != coupon) {
            coupon.setName(request.getCoupon().getName());
            coupon.setStartDate(request.getCoupon().getStartDate());
            coupon.setEndDate(request.getCoupon().getEndDate());
            coupon.setDiscount(request.getCoupon().getDiscount());

            couponRepository.save(coupon);

            return CouponDTO.builder()
            .id(Long.toString(coupon.getCouponId()))
            .name(coupon.getName())
            .startDate(coupon.getStartDate())
            .endDate(coupon.getEndDate())
            .discount(coupon.getDiscount())
            .build();
        }

        return null;
    }


    public CouponDTO create(CouponRequest request) {

        
        Coupon coupon= couponRepository.findByName(request.getCoupon().getName())
        .orElseThrow(()->new RuntimeException("Coupon already exist. "));
        
        if (null != coupon) {
            return CouponDTO.builder()
            .id(Long.toString(coupon.getCouponId()))
            .name(coupon.getName())
            .startDate(coupon.getStartDate())
            .endDate(coupon.getEndDate())
            .discount(coupon.getDiscount())
            .build();
        }

        Coupon newCoupon = Coupon.builder()
        .name(request.getCoupon().getName())
            .startDate(request.getCoupon().getStartDate())
            .endDate(request.getCoupon().getEndDate())
            .discount(request.getCoupon().getDiscount())
            .build();

        couponRepository.save(newCoupon);

         return CouponDTO.builder()
        .id(Long.toString(newCoupon.getCouponId()))
        .name(newCoupon.getName())
        .startDate(newCoupon.getStartDate())
        .endDate(newCoupon.getEndDate())
        .discount(newCoupon.getDiscount())
        .build();
    }

}
