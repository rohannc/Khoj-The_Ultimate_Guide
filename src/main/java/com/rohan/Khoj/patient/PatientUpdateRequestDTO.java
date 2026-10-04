package com.rohan.Khoj.patient;

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

import com.rohan.Khoj.common.MobileNumberWrapperDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequestDTO {

    // --- Base User Fields ---
    // username and emailId are immutable and should not be updated via this endpoint.


    // --- Patient Specific Fields ---
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(Male|Female|Other)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Gender must be Male, Female, or Other")
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

    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    @Pattern(regexp = "\\d+", message = "Mobile number must contain only digits")
    private String primaryMobile;

    @Size(min = 2, max = 3, message = "Blood group must be between 2 and 3 characters (e.g., A+, O-)")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format (e.g., A+, O-).")
    private String bloodGroup;

    // Fields like ID, createdAt, updatedAt are not included as they are not updated by client.
}
