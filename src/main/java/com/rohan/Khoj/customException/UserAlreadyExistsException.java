package com.rohan.Khoj.customException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to be thrown when a registration attempt is made
 * with a username or email that already exists in the system.
 *
 * Annotating with @ResponseStatus tells Spring to return a 409 CONFLICT
 * status code if this exception is thrown.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
