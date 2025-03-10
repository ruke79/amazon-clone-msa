package com.project.coupon_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.common.constants.CouponStatus;
import com.project.coupon_service.model.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
    Optional<Coupon> findByNameAndUserIdAndCouponStatus(String name, long userId, CouponStatus couponStatus);
    Optional<List<Coupon>> findByUserIdAndCouponStatus(long userId, CouponStatus couponStatus);

    boolean existsByNameAndUserId(String name, long userId);
    
}
