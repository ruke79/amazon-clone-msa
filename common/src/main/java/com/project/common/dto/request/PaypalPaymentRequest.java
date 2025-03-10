package com.project.common.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.common.constants.OrderStatus;
import com.project.common.constants.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaypalPaymentRequest {

    private String orderId;

    private String userEmail;

    private String trackingId;

    private String paypalOrderId;

    private String orderStatus;

    private String couponName;

    private BigDecimal amounts;

    private String paymentStatus;

    private LocalDateTime createdTime;
}
