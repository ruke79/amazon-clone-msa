package com.project.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentResultStatus {

    Y("결제완료"),
    N("결제취소");

    private final String value;
}
