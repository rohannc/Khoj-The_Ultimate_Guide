package com.rohan.Khoj.doctor;

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

import com.rohan.Khoj.common.MobileNumberWrapperDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequestDTO {

    // --- Base User Fields ---
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username; // Updatable, uniqueness check in service


    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String emailId; // Updatable, uniqueness check in service

    // --- Doctor Specific Fields ---
    @Size(max = 100, message = "First name cannot be blank")
    private String firstName;

    @Size(max = 100, message = "Last name cannot be blank")
    private String lastName;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be 'Male', 'Female', or 'Other'")
    private String gender;

    @Size(max = 255, message = "Specialization too long")
    private String specializations;

    @Size(max = 500, message = "Qualifications too long")
    private String qualifications;

    private Integer experienceYears; // Use Integer for optionality (can be null)

    @PastOrPresent(message = "Registration issue date cannot be in the future.")
    private LocalDate registrationIssueDate;

    @Size(max = 50, message = "Medical license number too long")
    private String registrationNumber; // Updatable, uniqueness check in service

    @NotNull(message = "Primary mobile number is required")
    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    @Pattern(regexp = "\\d+", message = "Mobile number must contain only digits")
    private String primaryMobile;

}