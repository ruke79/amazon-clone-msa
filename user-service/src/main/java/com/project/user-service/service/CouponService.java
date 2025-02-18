package com.project.user-service.service;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.user-service.dto.CouponDTO;
import com.project.user-service.model.Coupon;
import com.project.user-service.repository.CouponRepository;
import com.project.user-service.security.request.CouponRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public boolean delete(String id) {

        Coupon coupon = couponRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Coupon not found. "));

        if (null != coupon) {
            couponRepository.delete(coupon);
            return true;
        }

        return false;
    }

    public CouponDTO update(CouponRequest request) {

        Long id = Long.parseLong(request.getCoupon().getId());
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found. "));

        if (null != coupon) {
            coupon.setName(request.getCoupon().getName());
            coupon.setStartDate(request.getCoupon().getStartDate());
            coupon.setEndDate(request.getCoupon().getEndDate());
            coupon.setDiscount(Integer.parseInt(request.getCoupon().getDiscount()));

            couponRepository.save(coupon);

            return CouponDTO.builder()
                    .id(Long.toString(coupon.getCouponId()))
                    .name(coupon.getName())
                    .startDate(coupon.getStartDate())
                    .endDate(coupon.getEndDate())
                    .discount(Integer.toString(coupon.getDiscount()))
                    .build();
        }

        return null;
    }

    private CouponDTO convertToDTO(Coupon coupon) {
        return CouponDTO.builder()
                .id(Long.toString(coupon.getCouponId()))
                .name(coupon.getName())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .discount(Integer.toString(coupon.getDiscount()))
                .build();

    }

    public List<CouponDTO> getCoupons() {

        List<Coupon> coupons = couponRepository.findAll();

        List<CouponDTO> result = new ArrayList<CouponDTO>();
        for (Coupon coupon : coupons) {

            result.add(convertToDTO(coupon));
        }

        return result;
    }

    public boolean create(CouponRequest request) {

        Optional<Coupon> data = couponRepository.findByName(request.getCoupon().getName());

        if (data.isPresent()) {

            return false;
        }

        Coupon newCoupon = Coupon.builder()
                .name(request.getCoupon().getName())
                .startDate(request.getCoupon().getStartDate())
                .endDate(request.getCoupon().getEndDate())
                .discount(Integer.parseInt(request.getCoupon().getDiscount()))
                .build();

        couponRepository.save(newCoupon);

        return true;
    }

}
