package com.project.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentResultStatus {

    SUCCESS("결제완료"),
    REFUND("환불완료"),
    WAITING_FOR_PAYMENT("결제대기");
    
    private String status;

    PaymentResultStatus(String status) {
        this.status = status;
    }

}
