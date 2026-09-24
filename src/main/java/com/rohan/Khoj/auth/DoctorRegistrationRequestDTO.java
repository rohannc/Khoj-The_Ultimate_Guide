package com.rohan.Khoj.auth;

import com.rohan.Khoj.common.MobileNumberWrapperDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegistrationRequestDTO extends BaseRegistrationRequestDTO {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name too long")
    private String firstName;

    @Size(max = 100, message = "Last name too long")
    private String lastName; // Optional in DTO

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be 'Male', 'Female', or 'Other'")
    private String gender;

    @NotBlank(message = "Specializations cannot be blank")
    @Size(max = 255, message = "Specializations too long")
    private String specializations;

    @NotBlank(message = "Qualifications cannot be blank")
    @Size(max = 500, message = "Qualifications too long")
    private String qualifications;

    @NotBlank(message = "Registration number cannot be blank")
    @Size(max = 50, message = "Registration number too long")
    private String registrationNumber;

    // --- Registration Issue Date Validation ---
    @NotNull(message = "Registration issue date cannot be null.")
    @PastOrPresent(message = "Registration issue date cannot be in the future.")
    private LocalDate registrationIssueDate;

}