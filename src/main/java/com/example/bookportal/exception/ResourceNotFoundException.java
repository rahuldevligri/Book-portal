package com.example.bookportal.exception;

public class ResourceNotFoundException extends RuntimeException {
    /**
     * Exception thrown when a resource is not found.
     * 
     * @param message the error message
     */
    public ResourceNotFoundException(String message) {

        super(message);
    }
}
