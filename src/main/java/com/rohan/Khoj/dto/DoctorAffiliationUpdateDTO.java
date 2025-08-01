package com.rohan.Khoj.dto;

import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
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
public class DoctorAffiliationUpdateDTO {

    @NotNull(message = "Affiliation ID cannot be null")
    private DoctorClinicAffiliationId affiliationId;

    @NotNull(message = "Status action cannot be null")
    private String statusAction; // "ACCEPT", "REJECT", "UPDATE"

    // New field to specify patient limits per slot
    @NotNull(message = "Patient limits cannot be null")
    private Integer patientLimits;

    private Double doctorCharge;
    private Map<String, String> shiftDetails;
    private LocalDate joiningDate;
}