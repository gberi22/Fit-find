package com.fitfind.fitfind.security.exception.handler;

import com.fitfind.fitfind.ai.recommendation.exception.CategoryFailedException;
import com.fitfind.fitfind.ai.recommendation.exception.InvalidReferenceImageException;
import com.fitfind.fitfind.client.exception.ClientNotFoundException;
import com.fitfind.fitfind.ai.imagegen.exception.ImageGenerationException;
import com.fitfind.fitfind.registration.exception.EmailAlreadyExistsException;
import com.fitfind.fitfind.security.exception.model.ApiErrors;
import com.fitfind.fitfind.security.ratelimit.exception.TooManyRequestException;
import com.fitfind.fitfind.wardrobe.exception.LookNotFoundException;
import com.fitfind.fitfind.websearch.exception.TransientSearchException;
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

    @ExceptionHandler(ImageGenerationException.class)
    public ResponseEntity<ApiErrors> handleImageGenerationException(ImageGenerationException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InvalidReferenceImageException.class)
    public ResponseEntity<ApiErrors> handleInvalidReferenceImageException(InvalidReferenceImageException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrors> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransientSearchException.class)
    public ResponseEntity<ApiErrors> handleTransientSearchException(TransientSearchException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(LookNotFoundException.class)
    public ResponseEntity<ApiErrors> handleLookNotFoundException(LookNotFoundException ex) {
        ApiErrors apiErrors = ApiErrors.builder()
                .withMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(apiErrors, HttpStatus.NOT_FOUND);
    }
}
