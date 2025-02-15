package com.project.chatserver.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // DTO 를 JSON으로 변환 시 null값인 field 제외
public class ResponseDTO {
    private Integer status;
    private Object data;

    public ResponseDTO(Integer status) {
        this.status = status;
    }

    public static ResponseDTO addStatus(Integer status) {
        return new ResponseDTO(status);
    }
    // public static StatusResponseDto addStatus(Integer status, Chat data) {
    //     return new StatusResponseDto(status, data);
    // }

    public static ResponseDTO success(){
        return new ResponseDTO(200);
    }
    public static ResponseDTO success(Object data){
        return new ResponseDTO(200, data);
    }
}

