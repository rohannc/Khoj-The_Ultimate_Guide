package com.rohan.Khoj.healthrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecordDTO {
    private UUID id;
    private UUID patientId;
    private String documentTitle;
    private String documentType;
    private String documentUrl;
    private LocalDate testDate;
    private LocalDateTime uploadedAt;
}
