package com.project.coupon_service.model;

import java.time.LocalDateTime;

import com.project.common.constants.CouponStatus;

import io.hypersistence.utils.hibernate.id.Tsid;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="coupon")
public class Coupon extends BaseEntity{

    @Id @Tsid    
    @Column(name="coupon_id")    
    private Long couponId;

    private Long userId;

    private String name;

    @Column(name="start_date")
    private LocalDateTime startDate;

    @Column(name="end_date")
    private LocalDateTime endDate;

    private int discount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private CouponStatus couponStatus;

}
