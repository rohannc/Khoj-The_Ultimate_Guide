package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private String status; // e.g., Scheduled, Confirmed, Cancelled, Completed

    // Simplified representation of related entities
    private UUID patientId;
    private String patientFullName; // e.g., Patient's first name + last name

    private UUID doctorId;
    private String doctorFullName; // e.g., Doctor's first name + last name
    private String doctorSpecialization; // Assuming DoctorEntity has specialization

    private UUID clinicId;
    private String clinicName; // Assuming ClinicEntity has name

    // Add createdAt, updatedAt if your AppointmentDetailEntity has them and you want to expose
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;
}