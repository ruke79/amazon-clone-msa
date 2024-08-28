package com.project.backend.security.request;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import com.project.backend.constants.OrderStatusEnum;
import com.project.backend.constants.PayType;
import com.project.backend.constants.PaymentResultStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayRequest {

     private PayType payType = PayType.C;
     private OrderStatusEnum orderStatus = OrderStatusEnum.NOT_PROCESSED;

     private String orderNumber;
     private int orderPrice;

     @NotNull(message = "결제금액을 입력해주세요.")
     private Integer payPrice;

     @NotNull
    private PaymentResultStatus payStatus;

    private LocalDateTime payDateTime;
    private LocalDateTime payCancelDateTime;
}
