package com.rohan.Khoj.dto.registration;

import com.rohan.Khoj.dto.MobileNumberWrapperDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegistrationRequestDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String emailId;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name too long")
    private String firstName;

    @Size(max = 100, message = "Last name too long")
    private String lastName; // Optional in DTO

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be 'Male', 'Female', or 'Other'")
    private String gender;

    @NotEmpty(message = "Specialization cannot be blank")
    @Size(max = 255, message = "Specialization too long")
    private Set<String> specialization;

    @NotEmpty(message = "Qualifications cannot be blank")
    @Size(max = 500, message = "Qualifications too long")
    private Set<String> qualifications;

    @NotBlank(message = "Registration number cannot be blank")
    @Size(max = 50, message = "Registration number too long")
    private String registrationNumber;

    // --- Mobile Number Validation ---
    // @NotEmpty ensures at least one MobileNumberWrapperDto is present in the set.
    // @Valid ensures that validation is performed on each element within the set.
    @NotEmpty(message = "At least one mobile number is required.")
    @Valid // This is crucial to trigger validation on each MobileNumberWrapperDto object
    private Set<MobileNumberWrapperDTO> phoneNumbers;

    // --- Registration Issue Date Validation ---
    @NotNull(message = "Registration issue date cannot be null.")
    @PastOrPresent(message = "Registration issue date cannot be in the future.")
    private LocalDate registrationIssueDate;

}