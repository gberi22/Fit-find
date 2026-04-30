package com.fitfind.fitfind.security.exception.handler;

import com.fitfind.fitfind.model.exception.ClientNotFoundException;
import com.fitfind.fitfind.security.exception.model.ApiErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ClientExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiErrors> handleClientNotFoundException(ClientNotFoundException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.NOT_FOUND);
    }
}
