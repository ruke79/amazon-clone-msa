package com.project.coupon_service.service;

import java.lang.StackWalker.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.constants.CouponStatus;
import com.project.common.dto.SharedUserDto;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.CouponUseRequest;
import com.project.common.util.EmailValidator;
import com.project.coupon_service.client.UserServiceClient;
import com.project.coupon_service.dto.CouponDto;
import com.project.coupon_service.model.Coupon;
import com.project.coupon_service.repository.CouponRepository;
import com.project.coupon_service.dto.request.CouponApplyRequest;
import com.project.coupon_service.dto.request.CouponRequest;
import com.project.coupon_service.dto.response.CouponResponse;
import com.project.coupon_service.message.producer.CouponUseProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserServiceClient userServiceClient;
    private final CouponUseProducer couponUseProducer;
    

    public CouponResponse applyCoupon(CouponApplyRequest request) {

        
        ObjectMapper mapper = new ObjectMapper();
            SharedUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(request.getUserEmail()).getBody(), 
            SharedUserDto.class);

        if (null != response) {

                Coupon coupon = getCouponNotUsed(request.getCouponName(), response.getUserId());

                if (null != coupon) {            
                    

                    BigDecimal hundred = new BigDecimal(100);
                    BigDecimal afterDiscount = new BigDecimal(100 - coupon.getDiscount());
                                    
                    BigDecimal totalAfterDiscount = request.getTotalPrice().multiply(afterDiscount).divide(hundred,2, 
                    RoundingMode.HALF_UP);

                    couponUseProducer.publish(CouponUseRequest.builder()
                    .userId(response.getUserId())
                    .totalAfterDiscount(totalAfterDiscount)
                    .discount(coupon.getDiscount())
                    .build());
                
                    CouponResponse result = new CouponResponse(totalAfterDiscount, coupon.getDiscount(), "");

                    return result;
                }
        }

        CouponResponse result = new CouponResponse();
        result.setMessage("Failed to get coupons");

        return result;

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

    public CouponDto update(CouponRequest request) {

        // Long id = Long.parseLong(request.getCoupon().getId());
        // Coupon coupon = couponRepository.findById(id)
        //         .orElseThrow(() -> new RuntimeException("Coupon not found. "));

        // if (null != coupon) {
        //     coupon.setName(request.getCoupon().getName());
        //     coupon.setStartDate(request.getCoupon().getStartDate());
        //     coupon.setEndDate(request.getCoupon().getEndDate());
        //     coupon.setDiscount(request.getCoupon().getDiscount());

        //     couponRepository.save(coupon);

        //     return CouponDto.builder()
        //             .id(Long.toString(coupon.getCouponId()))
        //             .name(coupon.getName())
        //             .startDate(coupon.getStartDate())
        //             .endDate(coupon.getEndDate())
        //             .discount(coupon.getDiscount())
        //             .build();
        // }

        return null;
    }

    

    public List<CouponDto> getCoupons() {

        List<Coupon> coupons = couponRepository.findAll();

        List<CouponDto> result = new ArrayList<CouponDto>();
        for (Coupon coupon : coupons) {

            result.add(CouponDto.convertToDto(coupon));
        }

        return result;
    }

    @Transactional(readOnly=true)
    public boolean existsCoupon(String couponName, Long userId) {

        boolean existCoupon = couponRepository.existsByNameAndUserId(couponName, userId);

        return existCoupon;
    }

    public Coupon create(CouponRequest request) {

                
        boolean existCoupon = false; 

        if (EmailValidator.isValidEmail(request.getUserId())) {

            ObjectMapper mapper = new ObjectMapper();
            SharedUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(request.getUserId()).getBody(), 
            SharedUserDto.class);

            existCoupon = existsCoupon(request.getName(), response.getUserId());
        }
        else {
            existCoupon = existsCoupon(request.getName(), Long.parseLong(request.getUserId()));
        }

        if (!existCoupon) {        

            Coupon newCoupon = Coupon.builder()
                    .name(request.getName())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .discount(request.getDiscount())
                    .userId(Long.parseLong(request.getUserId()))
                    .couponStatus(CouponStatus.NOT_USED)
                    .build();

            newCoupon = couponRepository.save(newCoupon);

            return newCoupon;
        }
        return null;
    }


    public Coupon getCouponNotUsed(String couponName, Long userId) {

        Coupon coupon = getCouponByNameAndUserIdAndCouponStatus(couponName, userId, CouponStatus.NOT_USED);

        log.info("Coupon : {} {}", couponName, userId);

        if ( null != coupon) {
            log.info("Coupon : {}", coupon.getName());
        }

        return coupon;
        
    }

    
    public List<CouponDto> getCouponsNotUsed(String email) {

        ObjectMapper mapper = new ObjectMapper();
            SharedUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(email).getBody(), 
            SharedUserDto.class);

        if (null != response) {

            List<Coupon> coupons = getCouponsByUserIdAndCouponStatus(response.getUserId(), CouponStatus.NOT_USED);
            
            if (!coupons.isEmpty()) {

                log.info("Coupon");
                List<CouponDto> result = new ArrayList<CouponDto>();
                for (Coupon coupon : coupons) {
        
                    result.add(CouponDto.convertToDto(coupon));
                }

                return result;
            }
            
        }

        log.info("Coupon is not found");
               
        return null;
    }

    @Transactional(readOnly=true)
    Coupon getCouponByNameAndUserIdAndCouponStatus(String couponName, Long userId, CouponStatus couponStatus) {

        Coupon coupon = couponRepository.findByNameAndUserIdAndCouponStatus(couponName, userId, couponStatus).orElse(null);

        return coupon;
    }

    @Transactional(readOnly=true)
    List<Coupon> getCouponsByUserIdAndCouponStatus(Long userId, CouponStatus couponStatus) {

        List<Coupon> coupons = couponRepository.findByUserIdAndCouponStatus(userId, couponStatus).orElse(null);

        return coupons;
    }

    @Transactional 
    public Coupon couponRollback(CouponRollbackRequest request) {

        Coupon coupon = getCouponByNameAndUserIdAndCouponStatus(
            request.getCouponName(), request.getUserId(), CouponStatus.USED);

        if (null != coupon) {

            coupon.setCouponStatus(CouponStatus.NOT_USED);

            coupon = couponRepository.save(coupon);

            return coupon;
        }

        return null;
    }


}
