package com.project.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatusEnum {
    NOT_PROCESSED, 
    PROCESSING,
    DISPATCHED,
    CANCELED,
    COMPLETED,
    
}
