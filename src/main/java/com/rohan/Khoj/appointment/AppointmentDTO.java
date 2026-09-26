package com.rohan.Khoj.appointment;

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
    @io.swagger.v3.oas.annotations.media.Schema(description = "Queue token number assigned by clinic (integer)", example = "5")
    private Integer tokenNumber;
    private String reason;
    private String status;

    private String patientFullName;

    private String doctorFullName;
    // Included doctor's specialization in the response DTO
    private Set<String> doctorSpecialization;

    private String clinicName;
}