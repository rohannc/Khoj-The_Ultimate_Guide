package com.rohan.Khoj.affiliation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rohan.Khoj.affiliation.AffiliationStatus;

import java.util.Map;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliationResponseDTO {
    private UUID affiliationId;
    private AffiliationStatus status;
    private String message;
    private UUID doctorId;
    private UUID clinicId;
    private Double doctorCharge;
    private Double clinicCharge;
    private Map<String, String> shiftDetails;
    private LocalDate joiningDate;
    private Long version;
    private Integer patientLimits;
}