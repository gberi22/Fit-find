package com.fitfind.fitfind.ai.exception;

public class CategoryFailedException extends RuntimeException {

    public CategoryFailedException(String message) {
        super(message);
    }

    public CategoryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
