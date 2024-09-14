package com.project.backend.constants;

import java.util.Map;
import java.util.HashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Getter
@RequiredArgsConstructor
public enum PaymentResultStatus {

    PAID("paid"),
    UNPAID("unpaid"),
    PROCESSING("processing"),
    NOT_PROCESSED("unprocessed"),
    DISPATCHED("dispatched"),
    COMPLETED("completed"),
    CANCELLED("cancelled");
    
    private String status;
    
    private static class Holder {
        static Map<String, PaymentResultStatus> MAP = new HashMap<>();
    }

    private PaymentResultStatus(String status) {
        Holder.MAP.put(status, this);
    }

    public static PaymentResultStatus getStatus(String val) {
        PaymentResultStatus status = Holder.MAP.get(val);
        if(status == null) {
            
            throw new  IllegalStateException(String.format("Unsupported status %s.", val)); 
        }

        return status;        
    }

}
