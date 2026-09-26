package com.rohan.Khoj.vital;

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
public class VitalDTO {
    private UUID id;
    @Schema(description = "Systolic blood pressure", example = "120")
    private Integer systolicBp;
    @Schema(description = "Diastolic blood pressure", example = "80")
    private Integer diastolicBp;
    @Schema(description = "Height in centimeters", example = "175.5")
    private Double heightCm;
    @Schema(description = "Body Mass Index", example = "22.5")
    private Double bmi;
    private Integer heartRate;
    private Double weight;
    private Double temperature;
    private LocalDateTime recordedAt;
}
