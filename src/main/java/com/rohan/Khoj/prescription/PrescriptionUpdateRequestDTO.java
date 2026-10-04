package com.rohan.Khoj.prescription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating an individual medication")
public class PrescriptionUpdateRequestDTO {

    @NotBlank(message = "Medication name is required")
    @Schema(description = "Updated name of the medication", example = "Metformin ER")
    private String medicationName;

    @NotBlank(message = "Dosage is required")
    @Schema(description = "Updated dosage amount", example = "1000mg")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Schema(description = "Updated frequency", example = "Once daily with dinner")
    private String frequency;

    @Schema(description = "Updated medication start date", example = "2026-10-02")
    private LocalDate startedAt;

    @Schema(description = "Updated duration numeric value", example = "60")
    private Integer durationValue;

    @Schema(description = "Updated duration unit: DAY, WEEK, MONTH, YEAR, ONGOING", example = "DAY")
    private DurationUnit durationUnit;

    @Schema(description = "Updated patient directions/instructions", example = "Increased dose after review. Take with dinner.")
    private String instructions;

    @Schema(description = "Active status of the medication", example = "true")
    private Boolean isActive;
}
