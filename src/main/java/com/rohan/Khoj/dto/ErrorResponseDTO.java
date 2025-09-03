package com.rohan.Khoj.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A standard error response object using Lombok to reduce boilerplate code.
 * This DTO can be used for all error scenarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    /**
     * The timestamp of when the error occurred. Formatted for consistency.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * The HTTP status code (e.g., 404, 500).
     */
    private int status;

    /**
     * A short, standard HTTP error phrase (e.g., "Not Found", "Internal Server Error").
     */
    private String error;

    /**
     * A user-friendly message explaining what went wrong.
     */
    private String message;

    /**
     * The path of the request that resulted in an error.
     */
    private String path;

    /**
     * A custom constructor to set the timestamp automatically.
     * The @AllArgsConstructor will not handle this logic.
     * @param status The HTTP status code.
     * @param error The HTTP error phrase.
     * @param message The user-friendly message.
     * @param path The request path.
     */
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this(); // Sets the timestamp using the default constructor's logic
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
