// DoctorClinicAffiliationDTO.java
package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorClinicAffiliationDTO {
    private UUID id; // ID of the affiliation record itself
    private UUID doctorId;
    private String doctorFullName;
    private UUID clinicId;
    private String clinicName;
    private LocalDate joiningDate;
    private String roleInClinic;
    private String shiftDetails;
    private Double charge;
    // Add createdAt, updatedAt if your affiliation entity has them
}