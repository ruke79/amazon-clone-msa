package com.project.common.message.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponUseRequest implements Serializable {

    private Long userId;
    private int discount;    
    private BigDecimal totalAfterDiscount;
}
