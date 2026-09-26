package com.rohan.Khoj.prescription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating the start date of a prescribed medication")
public class PrescriptionStartDateUpdateRequestDTO {

    @NotNull(message = "Start date cannot be null")
    @Schema(description = "The date when the patient started or will start the medication", example = "2026-09-28")
    private LocalDate startedAt;
}
