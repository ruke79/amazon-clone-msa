package com.project.common.message.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

import com.project.common.constants.PaymentStatus;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse implements Serializable {
    
    private Long  paymentId;
    private Long  orderId;    
    private Long  customerId;
    private BigDecimal amounts;
    private PaymentStatus paymentStatus;    

}
