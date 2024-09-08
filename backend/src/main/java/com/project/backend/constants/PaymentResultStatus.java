package com.project.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentResultStatus {

    SUCCESS("SUCCESS"),
    REFUND("REFUND"),
    WAITING_FOR_PAYMENT("WAITING");
    
    private String status;

    PaymentResultStatus(String status) {
        this.status = status;
    }

}
