package com.project.common.constants;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {    
    
    NOT_PROCESSED("NOT_PROCESSED"), 
    PAID("PAID"),
    UNPAID("UNPAID"),       
    PROCESSING("PROCESSING"),
    DISPATCHED("DISPATCHED"),
    DELIVERED("DELIVERED"),
    CANCELED("CANCELED"),
    COMPLETED("COMPLETED");
    

    private static class Holder {
        static Map<String, OrderStatus> MAP = new HashMap<>();
    }

    private OrderStatus(String status) {
        Holder.MAP.put(status, this);
    }

    public static OrderStatus getStatus(String val) {
        OrderStatus status = Holder.MAP.get(val);
        if(status == null) {
            
            throw new  IllegalStateException(String.format("Unsupported status %s.", val)); 
        }

        return status;        
    }
}
