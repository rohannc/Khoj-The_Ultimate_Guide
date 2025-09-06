package com.rohan.Khoj.customException;

// No Spring-specific annotations are needed here.
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}