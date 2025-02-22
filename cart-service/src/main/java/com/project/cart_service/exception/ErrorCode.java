package com.project.cart_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "해당 회원 정보를 찾을 수 없습니다.", "015"),
   
    UNAUTHORIZED_TOKEN_EXCEPTION(UNAUTHORIZED, "다시  시작해주세요.", "028");

    private final HttpStatus httpStatus;
    private final String detail;
    private final String errorCode;

    
    public static CustomException throwUserrNotFound() {
        throw new CustomException(USER_NOT_FOUND);
    }
    public static CustomException unauthorizedTokenException() {
        throw new CustomException(UNAUTHORIZED_TOKEN_EXCEPTION);
    }

}
