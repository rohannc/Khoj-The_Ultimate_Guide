package com.rohan.Khoj.affiliation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliationUpdateDTO {

    private UUID affiliationId;

    @NotBlank(message = "Status action (ACCEPT, REJECT, UPDATE) is required")
    private String statusAction;

    // Optional fields for updates/acceptances
    private Double charge;
    private LocalDate joiningDate;
    private Map<String, String> shiftDetails;
    private Integer patientLimits;
}
