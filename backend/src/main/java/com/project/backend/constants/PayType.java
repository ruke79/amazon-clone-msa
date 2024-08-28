package com.project.backend.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayType {
    C("카드결제"),
    L("현장결제"),  // 가게 사장님이랑 협의
    A("계좌이체");  // 가게 사장님이랑 협의

    private final String value;
}
