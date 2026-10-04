package com.rohan.Khoj.prescription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for adding an individual medication for a patient")
public class PrescriptionCreateRequestDTO {

    @NotNull(message = "Patient ID is required")
    @Schema(description = "UUID of the patient", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID patientId;

    @NotBlank(message = "Medication name is required")
    @Schema(description = "Name and strength of the medication", example = "Metformin")
    private String medicationName;

    @NotBlank(message = "Dosage is required")
    @Schema(description = "Dosage amount", example = "500mg")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Schema(description = "Frequency of administration", example = "Twice daily with food")
    private String frequency;

    @Schema(description = "Date when the medication should start or started", example = "2026-10-02")
    private LocalDate startedAt;

    @Schema(description = "Duration numeric value", example = "30")
    private Integer durationValue;

    @Schema(description = "Unit for duration: DAY, WEEK, MONTH, YEAR, ONGOING", example = "DAY")
    private DurationUnit durationUnit;

    @Schema(description = "Specific patient instructions for this medication", example = "Take after meals. Monitor blood glucose.")
    private String instructions;

    @Builder.Default
    @Schema(description = "Whether the medication is currently active", example = "true")
    private Boolean isActive = true;
}
