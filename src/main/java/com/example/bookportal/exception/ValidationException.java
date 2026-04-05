package com.example.bookportal.exception;

public class ValidationException extends RuntimeException {
    /**
     * Exception thrown for validation errors.
     * 
     * @param message the error message
     */
    public ValidationException(String message) {

        super(message);
    }
}
