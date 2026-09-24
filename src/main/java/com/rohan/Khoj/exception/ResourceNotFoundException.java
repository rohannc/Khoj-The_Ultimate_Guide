package com.rohan.Khoj.exception;

// No Spring-specific annotations are needed here.
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}