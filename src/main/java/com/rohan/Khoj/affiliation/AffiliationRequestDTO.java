package com.rohan.Khoj.affiliation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliationRequestDTO {

    @NotNull(message = "Target ID cannot be null")
    private UUID targetId; // If initiator is a doctor, this is the clinic ID, and vice-versa

    @NotNull(message = "Joining date cannot be null")
    @Future(message = "Joining date must be in the future")
    private LocalDate joiningDate;

    @NotEmpty(message = "Shift details cannot be blank")
    @Size(max = 7, message = "Shift details must be provided for at most 7 days.")
    private Map<String, String> shiftDetails;

    @NotNull(message = "Charge cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Charge must be positive")
    private Double charge;

    @NotNull(message = "Patient limit cannot be null")
    @Min(value = 1, message = "Patient limit must be at least 1")
    private Integer patientLimits;
}
