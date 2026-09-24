package com.rohan.Khoj.auth;

import com.rohan.Khoj.common.MobileNumberWrapperDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicRegistrationRequestDTO extends BaseRegistrationRequestDTO {

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

    @Size(max = 255, message = "Website URL too long")
    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$", message = "Invalid website URL format")
    private String website;

    // Opening Hours: Often complex for initial registration.
    // Making it optional for registration, but you could make it @NotEmpty
    // if you require hours on first registration.
    @Size(max = 7, message = "Opening hours map can have at most 7 days (Monday-Sunday).")
    private Map<String, String> openingHours; // e.g., {"Monday": "9AM-5PM"}

}