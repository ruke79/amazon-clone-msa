package com.project.common.message.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.common.constants.OrderStatus;
import com.project.common.constants.PaymentStatus;
import com.project.common.constants.PaymentType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRequest implements Serializable {

    private Long orderId;

    private Long customerId;

    private String trackingId;

    private String paypalOrderId;

    private BigDecimal amounts;

    private OrderStatus orderStatus;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;
}
