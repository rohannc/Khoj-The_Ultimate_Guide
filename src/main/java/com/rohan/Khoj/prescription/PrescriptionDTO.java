package com.rohan.Khoj.prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {
    private UUID id;
    @NotBlank(message = "Field cannot be blank")

    private UUID patientId;
    private String patientName;
    private String doctorName;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String medicationName;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String dosage;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String frequency;
    
    @Schema(description = "Date when the patient started or is scheduled to start the medication", example = "2026-09-28")
    private java.time.LocalDate startedAt;

    @Schema(description = "Duration value (number)", example = "1")
    private Integer durationValue;

    @Schema(description = "Duration unit: DAY, WEEK, MONTH, YEAR, ONGOING", example = "MONTH")
    private DurationUnit durationUnit;

    @Schema(description = "Calculated medication end date based on startedAt and duration (null if ongoing or not started)", example = "2026-10-28")
    private java.time.LocalDate endDate;

    @Schema(description = "Specific directions or instructions for taking the medication", example = "Complete full course with warm water")
    private String instructions;

    @Schema(description = "Indicates whether the medication is currently active", example = "true")
    private Boolean isActive;

    @Schema(description = "Reason provided if medication was discontinued", example = "Patient reported allergy")
    private String discontinueReason;

    @Schema(description = "Timestamp when the prescription was issued")
    private LocalDateTime issuedAt;
}
