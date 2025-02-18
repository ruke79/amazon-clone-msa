package com.project.user-service.security.request;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import com.project.user-service.constants.OrderStatusEnum;

import com.project.user-service.constants.PaymentResultStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayRequest {

     private String id;
     private String email_address;
     private OrderStatusEnum orderStatus = OrderStatusEnum.NOT_PROCESSED;

     private String orderNumber;
     private int orderPrice;

     //@NotNull
     //private Integer payPrice;

    @NotNull
    private PaymentResultStatus payStatus;

    private LocalDateTime payDateTime;
    private LocalDateTime payCancelDateTime;
}
