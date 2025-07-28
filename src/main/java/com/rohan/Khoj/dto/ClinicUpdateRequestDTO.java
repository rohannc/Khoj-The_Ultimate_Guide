package com.rohan.Khoj.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicUpdateRequestDTO {

    // --- Base User Fields ---
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // Updatable, uniqueness check in service

    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    private String password; // New password to be hashed in service

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String email; // Updatable, uniqueness check in service

    // --- Clinic Specific Fields ---
    @Size(max = 255, message = "Clinic name too long")
    private String name; // Name of the clinic

    @Size(max = 255, message = "Street address too long")
    private String street;

    @Size(max = 100, message = "City name too long")
    private String city;

    @Size(max = 100, message = "State name too long")
    private String state;

    @Size(min = 6, max = 6, message = "Pin code must be exactly 6 digits long")
    @Pattern(regexp = "\\d+", message = "Pin code must contain only digits")
    private String pinCode;

    @Size(max = 100, message = "Country name too long")
    private String country;

    // --- Phone Numbers: At least one required if provided, each validated ---
    @NotEmpty(message = "At least one mobile number is required if updating mobile numbers.")
    @Valid // Crucial: Enables validation on elements within the Set
    private Set<MobileNumberWrapperDto> phoneNumbers;

    @Size(max = 255, message = "Website URL too long")
    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$", message = "Invalid website URL format")
    private String website;

    @Size(max = 7, message = "Opening hours map can have at most 7 days (Monday-Sunday).")
    private Map<String, String> openingHours;

    // --- MobileNumberWrapperDto (Inner Static Class) ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MobileNumberWrapperDto {
        @NotNull(message = "Mobile number cannot be null.")
        @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits.")
        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,15}$", message = "Invalid mobile number format. Must contain only digits, spaces, hyphens, parentheses and optional '+' prefix.")
        private String number;
    }
}