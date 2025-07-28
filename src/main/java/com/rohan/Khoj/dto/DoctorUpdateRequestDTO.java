package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequestDTO {

    // --- Base User Fields ---
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // Updatable, uniqueness check in service

    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    private String password; // New password to be hashed in service

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String email; // Updatable, uniqueness check in service

    // --- Doctor Specific Fields ---
    @Size(max = 100, message = "First name cannot be blank")
    private String firstName;

    @Size(max = 100, message = "Last name cannot be blank")
    private String lastName;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be 'Male', 'Female', or 'Other'")
    private String gender;

    @Size(max = 255, message = "Specialization too long")
    private String specialization;

    @Size(max = 500, message = "Qualifications too long")
    private String qualifications;

    private Integer experienceYears; // Use Integer for optionality (can be null)

    @PastOrPresent(message = "Registration issue date cannot be in the future.")
    private LocalDate registrationIssueDate;

    @Size(max = 50, message = "Medical license number too long")
    private String registrationNumber; // Updatable, uniqueness check in service

    // --- Phone Numbers: At least one required if provided, each validated ---
    @NotEmpty(message = "At least one mobile number is required if updating mobile numbers.")
    @Valid // Crucial: Enables validation on elements within the Set
    private Set<MobileNumberWrapperDto> phoneNumbers;

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