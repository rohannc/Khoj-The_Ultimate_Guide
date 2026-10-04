package com.rohan.Khoj.affiliation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Request payload to update, counter-negotiate, accept, or reject an affiliation request")
public class AffiliationUpdateDTO {

    @Schema(description = "ID of the affiliation being updated or negotiated", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID affiliationId;

    @NotBlank(message = "Status action (ACCEPT, REJECT, UPDATE) is required")
    @Schema(description = "Action to execute: ACCEPT, REJECT, or UPDATE (counter-offer)", example = "ACCEPT")
    private String statusAction;

    @Schema(description = "Consultation fee for this counter-offer or upon acceptance", example = "600.00")
    private Double charge;

    @Schema(description = "Updated proposed joining date", example = "2026-11-15")
    private LocalDate joiningDate;

    @Schema(
            description = "Updated 7-day shift schedule (MONDAY to SUNDAY). Use 'HH:mm - HH:mm' or 'OFF' for off days.",
            example = "{\"MONDAY\": \"09:00 - 17:00\", \"TUESDAY\": \"09:00 - 17:00\", \"WEDNESDAY\": \"09:00 - 17:00\", \"THURSDAY\": \"09:00 - 17:00\", \"FRIDAY\": \"09:00 - 17:00\", \"SATURDAY\": \"10:00 - 14:00\", \"SUNDAY\": \"OFF\"}"
    )
    private Map<String, String> shiftDetails;

    @Schema(description = "Updated daily patient appointment limit", example = "25")
    private Integer patientLimits;
}
