package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid; // For nested validation
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty; // For collections
import jakarta.validation.constraints.NotNull; // For non-primitive types that cannot be null
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicRegistrationRequestDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    // Consider adding @Pattern for stronger password policies (e.g., requires digits, special chars)
    // @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and include digits, lower/upper case, and special characters.")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String emailId; // Renamed from emailId for input DTO

    @NotBlank(message = "Clinic name cannot be blank")
    @Size(max = 255, message = "Clinic name too long")
    private String name; // Corresponds to entity's 'name' field

    @NotBlank(message = "Street address cannot be blank")
    @Size(max = 255, message = "Street address too long")
    private String street;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City name too long")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State name too long")
    private String state;

    @NotBlank(message = "Pin code cannot be blank")
    @Size(min = 6, max = 6, message = "Pin code must be exactly 6 digits long") // Exact 6 digits
    @Pattern(regexp = "\\d+", message = "Pin code must contain only digits") // Numeric only
    private String pinCode;

    @Size(max = 100, message = "Country name too long")
    // @NotBlank if you want to make it mandatory, otherwise entity's default will apply if null
    private String country; // Default will be applied by entity if null

    // --- Phone Numbers with Wrapper DTO and Validation ---
    @NotEmpty(message = "At least one phone number is required.") // Ensures the set is not empty
    @Valid // Crucial: Enables validation on elements within the Set
    private Set<MobileNumberWrapperDTO> phoneNumbers;

    @Size(max = 255, message = "Website URL too long")
    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$", message = "Invalid website URL format")
    private String website;

    // Opening Hours: Often complex for initial registration.
    // Making it optional for registration, but you could make it @NotEmpty
    // if you require hours on first registration.
    @Size(max = 7, message = "Opening hours map can have at most 7 days (Monday-Sunday).")
    private Map<String, String> openingHours; // e.g., {"Monday": "9AM-5PM"}

}