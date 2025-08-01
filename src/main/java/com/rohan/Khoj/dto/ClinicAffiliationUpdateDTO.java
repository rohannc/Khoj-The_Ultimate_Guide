package com.rohan.Khoj.dto;

import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class ClinicAffiliationUpdateDTO {

    @NotNull(message = "Affiliation ID cannot be null")
    private DoctorClinicAffiliationId affiliationId;

    @NotNull(message = "Status action cannot be null")
    private String statusAction; // "ACCEPT", "REJECT", "UPDATE"

    private Double clinicCharge;
    private Map<String, String> shiftDetails;
    private LocalDate joiningDate;

    @Min(value = 1, message = "Patient limit must be at least 1")
    private Integer patientLimits;
}