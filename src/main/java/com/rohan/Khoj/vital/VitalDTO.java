package com.rohan.Khoj.vital;

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
public class VitalDTO {
    private UUID id;
    private UUID patientId;
    private String bloodPressure;
    private Integer heartRate;
    private Double weight;
    private Double temperature;
    private LocalDateTime recordedAt;
}
