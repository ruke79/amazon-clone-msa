package com.project.chatserver.common.exception.handler;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class  ApiResponse        
{

    private final ApiStatus status;
    private final String message;
    private final String errorCodeName;
    private final Object data;

    public static ApiResponse success(Object data) {
        return new ApiResponse(ApiStatus.SUCCESS, null, null, data);
    }
    public static ApiResponse error(String message, String errorCodeName) {
        return new ApiResponse(ApiStatus.ERROR, message, errorCodeName, null);
    }
}
