package com.example.webflux.exception;

import com.example.webflux.exception.CustomResponseEntity.ResponseStatusCodeEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Builder;
import lombok.experimental.Accessors;

@Builder
@Accessors(chain = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ResponseModel<T> {
    Header header;
    T data;

    @Builder
    @Accessors(chain = true)
    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    static class Header {
        String code;
        String message;
        boolean isSuccess;
    }

    static public <T> ResponseModel<T> success(T data) {
        ResponseStatusCodeEnum responseStatusCodeEnum = ResponseStatusCodeEnum.Success;
        Header header = Header.builder().code(responseStatusCodeEnum.getCode()).isSuccess(responseStatusCodeEnum.isSuccess()).message(responseStatusCodeEnum.getMessage()).build();
        return new ResponseModel<T>(header, data);
    }

    static public <T> ResponseModel<T> getResponse(ResponseStatusCodeEnum responseStatusCodeEnum, T data) {
        Header header = Header.builder().code(responseStatusCodeEnum.getCode()).isSuccess(responseStatusCodeEnum.isSuccess()).message(responseStatusCodeEnum.getMessage()).build();
        return new ResponseModel<T>(header, data);
    }
}
