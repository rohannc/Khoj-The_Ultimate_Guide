package com.rohan.Khoj.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@Builder // For easily building DTO instances
@NoArgsConstructor // For default constructor needed by Spring/Jackson
@AllArgsConstructor // For constructor with all fields needed by builder
public class DoctorAffiliationRequestDTO {

    @NotNull(message = "Clinic ID cannot be null")
    private UUID clinicId;

    @NotNull(message = "Joining date cannot be null")
    @Future(message = "Joining date cannot be in the future") // Affiliation usually starts on or before today
    private LocalDate joiningDate;

    @NotEmpty(message = "Shift details cannot be blank")
    @Size(max = 7, message = "Shift details must be provided for at most 7 days.")
    private Map<String, String> shiftDetails; // e.g., {"Monday": "9AM-5PM", "Tuesday": "10AM-6PM"}

    @NotNull(message = "Charge cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Charge must be positive") // Charge must be greater than 0
    private Double doctorCharge; // Using Double to allow @NotNull; entity uses primitive double which is auto-boxed/unboxed.

    // New field for a single patient limit for all slots
    @NotNull(message = "Patient limit cannot be null")
    @Min(value = 1, message = "Patient limit must be at least 1")
    private Integer patientLimits;

}