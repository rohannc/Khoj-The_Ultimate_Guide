package com.rohan.Khoj.auth;

import com.rohan.Khoj.common.MobileNumberWrapperDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegistrationRequestDTO extends BaseRegistrationRequestDTO {

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;

    @NotNull(message = "Date of birth cannot be null") // Date of birth is typically mandatory
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    // Address fields - consider making @NotBlank if mandatory for all patients
    @Size(max = 255, message = "Street address too long")
    private String street;

    @Size(max = 100, message = "City name too long")
    private String city;

    @Size(max = 100, message = "State name too long")
    private String state;

    @NotBlank(message = "Pin code cannot be blank") // Pin code is often mandatory
    @Size(min = 6, max = 6, message = "Pin code must be exactly 6 digits long") // Exact 6 digits
    @Pattern(regexp = "\\d+", message = "Pin code must contain only digits") // Numeric only
    private String pinCode;

    @Size(max = 100, message = "Country name too long")
    // If you want to enforce default "India" as entity, leave without @NotBlank
    // If you want it mandatory, add @NotBlank
    private String country;

    // --- Blood Group Validation ---
    @NotBlank(message = "Blood group cannot be blank") // Blood group is often mandatory
    @Size(min = 2, max = 3, message = "Blood group must be between 2 and 3 characters (e.g., A+, O-)")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format (e.g., A+, O-).")
    private String bloodGroup;

}