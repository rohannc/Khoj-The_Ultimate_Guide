package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private UUID id;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private String status;

    private UUID patientId;
    private String patientFullName;

    private UUID doctorId;
    private String doctorFullName;
    // Included doctor's specialization in the response DTO
    private Set<String> doctorSpecialization;

    private UUID clinicId;
    private String clinicName;
}