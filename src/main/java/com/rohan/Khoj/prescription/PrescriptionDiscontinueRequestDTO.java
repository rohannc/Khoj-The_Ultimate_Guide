package com.rohan.Khoj.prescription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for discontinuing a medication")
public class PrescriptionDiscontinueRequestDTO {

    @Schema(description = "Reason for discontinuing the medication", example = "Patient reported adverse symptoms / Completed course early")
    private String reason;
}
