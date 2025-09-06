package com.rohan.Khoj.dto.errorResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * A standard, flexible error response object.
 * Uses Lombok's @Data to reduce boilerplate.
 * Includes an optional map for detailed field-level validation errors.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // This ensures that any null fields (like fieldErrors) are not included in the JSON output.
public class ErrorResponseDTO {

    /**
     * The timestamp of when the error occurred. Formatted for consistency.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime timestamp;

    /**
     * The HTTP status code (e.g., 404, 500).
     */
    private final int status;

    /**
     * A short, standard HTTP error phrase (e.g., "Not Found", "Validation Failed").
     */
    private final String error;

    /**
     * A user-friendly message explaining what went wrong.
     */
    private final String message;

    /**
     * The path of the request that resulted in an error.
     */
    private final String path;

    /**
     * A map containing field-specific error messages, used for validation failures.
     * This field will be null and omitted from the JSON for non-validation errors.
     */
    private Map<String, String> fieldErrors;

    /**
     * Constructor for general errors (without field-specific details).
     * @param status The HTTP status code.
     * @param error The HTTP error phrase.
     * @param message The user-friendly message.
     * @param path The request path.
     */
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Constructor for validation errors, including field-specific details.
     * @param status The HTTP status code.
     * @param error The HTTP error phrase.
     * @param message The user-friendly message.
     * @param path The request path.
     * @param fieldErrors A map of invalid fields to their error messages.
     */
    public ErrorResponseDTO(int status, String error, String message, String path, Map<String, String> fieldErrors) {
        this(status, error, message, path); // Calls the main constructor
        this.fieldErrors = fieldErrors;
    }
}