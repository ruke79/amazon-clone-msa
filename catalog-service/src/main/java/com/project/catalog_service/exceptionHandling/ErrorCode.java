package com.project.catalog_service.exceptionHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    
    UN_SUPPORTED_FILE(UNSUPPORTED_MEDIA_TYPE, "지원하는 파일 형식이 아닙니다.", "001"),    
    MAX_FILE_SIZE_EXCEEDED(PAYLOAD_TOO_LARGE, "파일 크기가 허용되는 최대치를 초과하였습니다.", "002");
    

    private final HttpStatus httpStatus;
    private final String detail;
    private final String errorCode;
    
    public static CustomException throwUnSupportedFile() {
        throw new CustomException(UN_SUPPORTED_FILE);
    }
    
    public static CustomException throwMaxFileSizeExceeded() {
        throw new CustomException(MAX_FILE_SIZE_EXCEEDED);
    }


}
