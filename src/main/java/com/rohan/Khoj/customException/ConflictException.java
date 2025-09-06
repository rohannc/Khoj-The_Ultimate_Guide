package com.rohan.Khoj.customException;

// The @ResponseStatus annotation is removed to allow GlobalExceptionHandler to take full control.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}