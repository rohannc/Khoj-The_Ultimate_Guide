package com.rohan.Khoj.healthrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecordDTO {
    private UUID id;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String documentTitle;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String documentType;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String documentUrl;
    private LocalDate testDate;
    private LocalDateTime uploadedAt;
}
