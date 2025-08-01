package com.rohan.Khoj.dto;

import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.rohan.Khoj.entity.AffiliationStatus;

import java.util.Map;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliationResponseDTO {
    private DoctorClinicAffiliationId affiliationId;
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