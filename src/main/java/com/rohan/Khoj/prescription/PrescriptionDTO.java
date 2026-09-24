package com.rohan.Khoj.prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {
    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private String doctorName;
    private String medicationName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private Boolean isActive;
    private LocalDateTime issuedAt;
}
