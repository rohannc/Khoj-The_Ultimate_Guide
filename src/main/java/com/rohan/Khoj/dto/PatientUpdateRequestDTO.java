package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequestDTO {

    // --- Base User Fields ---
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // Updatable, uniqueness check in service

    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    // @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and include digits, lower/upper case, and special characters.")
    private String password; // New password to be hashed in service

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String email; // Updatable, uniqueness check in service

    // --- Patient Specific Fields ---
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

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
    @NotEmpty(message = "At least one phone number is required if updating phone numbers.")
    @Valid // Crucial: Enables validation on elements within the Set
    private Set<MobileNumberWrapperDto> phoneNumbers;

    @Size(min = 2, max = 3, message = "Blood group must be between 2 and 3 characters (e.g., A+, O-)")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format (e.g., A+, O-).")
    private String bloodGroup;

    // --- MobileNumberWrapperDto (Inner Static Class) ---
    // Reused from Registration DTOs for consistency in phone number validation.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MobileNumberWrapperDto {
        @NotNull(message = "Mobile number cannot be null.") // Changed from NotBlank for flexibility with 'empty' string
        @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits.")
        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,15}$", message = "Invalid mobile number format. Must contain only digits, spaces, hyphens, parentheses and optional '+' prefix.")
        private String number;
    }

    // Fields like ID, createdAt, updatedAt are not included as they are not updated by client.
}
