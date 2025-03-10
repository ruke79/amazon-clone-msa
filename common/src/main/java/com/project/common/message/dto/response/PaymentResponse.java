package com.project.common.message.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import com.project.common.constants.PaymentStatus;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse implements Serializable {
    
    private Long  paymentId;
    private Long  orderId;    
    private Long  customerId;
    private String couponName;
    private BigDecimal amounts;
    private PaymentStatus paymentStatus;    

}
