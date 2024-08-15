package com.example.webflux.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import com.example.webflux.exception.CustomResponseEntity.ResponseStatusCodeEnum;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseStatusExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseModel<Object>> handleException(Exception ex) {
        log.error("| handleException | error message : {} |", ex.getMessage());
        return CustomResponseEntity.getResponse(ResponseStatusCodeEnum.InTernalServerError, null);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
    public ResponseEntity<ResponseModel<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("| handleDataIntegrityViolationException | error message : {} |", ex.getMessage());
        if(ex instanceof DuplicateKeyException || ex.getMessage().contains("Duplicate entry")) {
            return CustomResponseEntity.getResponse(ResponseStatusCodeEnum.DuplicateKey, null);
        } else {
            return CustomResponseEntity.getResponse(ResponseStatusCodeEnum.SqlOrDataError, null);
        }
    }
}
