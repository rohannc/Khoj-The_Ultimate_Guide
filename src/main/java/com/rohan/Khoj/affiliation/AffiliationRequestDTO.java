package com.rohan.Khoj.affiliation;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload to create a new Doctor-Clinic affiliation request")
public class AffiliationRequestDTO {

    @NotNull(message = "Target ID cannot be null")
    @Schema(description = "ID of the recipient: Clinic ID if initiator is Doctor, or Doctor ID if initiator is Clinic", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID targetId;

    @NotNull(message = "Joining date cannot be null")
    @Future(message = "Joining date must be in the future")
    @Schema(description = "Proposed start/joining date for the doctor at the clinic", example = "2026-11-01")
    private LocalDate joiningDate;

    @NotEmpty(message = "Shift details cannot be blank")
    @Size(max = 7, message = "Shift details must be provided for at most 7 days.")
    @Schema(
            description = "Proposed shift schedule for all 7 days of the week (MONDAY to SUNDAY). Use 'HH:mm - HH:mm' or 'OFF'/'Closed' for off days.",
            example = "{\"MONDAY\": \"09:00 - 17:00\", \"TUESDAY\": \"09:00 - 17:00\", \"WEDNESDAY\": \"09:00 - 17:00\", \"THURSDAY\": \"09:00 - 17:00\", \"FRIDAY\": \"09:00 - 17:00\", \"SATURDAY\": \"10:00 - 14:00\", \"SUNDAY\": \"OFF\"}"
    )
    private Map<String, String> shiftDetails;

    @NotNull(message = "Charge cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Charge must be positive")
    @Schema(description = "Proposed consultation fee (doctor fee if proposed by doctor, clinic fee if proposed by clinic)", example = "500.00")
    private Double charge;

    @NotNull(message = "Patient limit cannot be null")
    @Min(value = 1, message = "Patient limit must be at least 1")
    @Schema(description = "Maximum daily patient appointment limit for this doctor at the clinic", example = "20")
    private Integer patientLimits;
}
