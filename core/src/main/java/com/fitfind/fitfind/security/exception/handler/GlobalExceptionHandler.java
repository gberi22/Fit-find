package com.fitfind.fitfind.security.exception.handler;

import com.fitfind.fitfind.ai.exception.CategoryFailedException;
import com.fitfind.fitfind.client.model.ClientNotFoundException;
import com.fitfind.fitfind.registration.exception.EmailAlreadyExistsException;
import com.fitfind.fitfind.security.exception.model.ApiErrors;
import com.fitfind.fitfind.security.ratelimit.exception.TooManyRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ApiErrors> handleClientNotFoundException(ClientNotFoundException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<ApiErrors> handleTooManyRequestException(TooManyRequestException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrors> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CategoryFailedException.class)
    public ResponseEntity<ApiErrors> handleCategoryFailedException(CategoryFailedException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.BAD_GATEWAY);
    }
}
