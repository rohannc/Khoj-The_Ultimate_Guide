package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder // For easily building DTO instances
@NoArgsConstructor // For default constructor needed by Spring/Jackson
@AllArgsConstructor // For constructor with all fields needed by builder
public class DoctorClinicAffiliationRequestDTO {

    @NotNull(message = "Doctor ID cannot be null")
    private UUID doctorId; // Corresponds to the doctor_id in the composite key

    @NotNull(message = "Clinic ID cannot be null")
    private UUID clinicId; // Corresponds to the clinic_id in the composite key

    @NotNull(message = "Joining date cannot be null")
    @PastOrPresent(message = "Joining date cannot be in the future") // Affiliation usually starts on or before today
    private LocalDate joiningDate;

    @NotBlank(message = "Role in clinic cannot be blank")
    @Size(max = 50, message = "Role in clinic must be at most 50 characters") // Matches entity column length
    private String roleInClinic;

    @NotBlank(message = "Shift details cannot be blank")
    @Size(max = 500, message = "Shift details too long") // A reasonable max length for DTO input
    private String shiftDetails;

    @NotNull(message = "Charge cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Charge must be positive") // Charge must be greater than 0
    private Double charge; // Using Double to allow @NotNull; entity uses primitive double which is auto-boxed/unboxed.
}