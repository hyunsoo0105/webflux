package com.example.webflux.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CustomResponseEntity<T> extends ResponseEntity<T> {

    public CustomResponseEntity(T body, HttpStatusCode status) {
        super(body, status);
    }

    @AllArgsConstructor
    static enum ResponseStatusCodeEnum {
        Success(HttpStatus.OK, true, "0000", ""),
        SqlOrDataError(HttpStatus.BAD_REQUEST, false, "0100", "Data Error"),
        DuplicateKey(HttpStatus.FOUND, false, "0101", "Already Exist"),
        InTernalServerError(HttpStatus.INTERNAL_SERVER_ERROR, false, "0500", "Internal Server Error"),
        ;
        @Getter
        HttpStatus httpStatus;
        @Getter
        boolean isSuccess;
        @Getter
        String code;
        @Getter
        String message;
    }

    static public <T> CustomResponseEntity<ResponseModel<T>> success(T data) {
        ResponseStatusCodeEnum responseStatusCodeEnum = ResponseStatusCodeEnum.Success;
        return new CustomResponseEntity<ResponseModel<T>>(ResponseModel.getResponse(responseStatusCodeEnum, data), responseStatusCodeEnum.getHttpStatus());
    }

    static public <T> CustomResponseEntity<ResponseModel<T>> getResponse(ResponseStatusCodeEnum responseStatusCodeEnum, T data) {
        return new CustomResponseEntity<ResponseModel<T>>(ResponseModel.getResponse(responseStatusCodeEnum, data), responseStatusCodeEnum.getHttpStatus());
    }
}
