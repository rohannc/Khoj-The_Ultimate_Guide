package com.rohan.Khoj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Nested DTO for validating individual phone numbers
// This allows applying @NotBlank, @Pattern, @Size to the 'number' string
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileNumberWrapperDTO {
    @NotBlank(message = "Mobile number cannot be blank.")
    @Size(min = 10, max = 20, message = "Mobile number must be between 10 and 20 characters.")
    // Common regex for numbers (might need to be more specific for international formats)
    // This regex allows for digits, spaces, hyphens, and parentheses, common in phone numbers.
    // For strict India numbers, use "^(\\+91[\\-\\s]?)?[0-9]{10}$"
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{7,20}$", message = "Invalid mobile number format.")
    private String number;
}
