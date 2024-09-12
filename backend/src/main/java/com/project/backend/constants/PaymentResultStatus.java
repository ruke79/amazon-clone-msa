package com.project.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentResultStatus {

    PAID("paid"),
    UNPAID("unpaid"),
    PROCESSING("Processing"),
    NOT_PROCESSED("Not Processed"),
    DISPATCHED("Dispatched"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");
    
    private String status;

    PaymentResultStatus(String status) {
        this.status = status;
    }

}
