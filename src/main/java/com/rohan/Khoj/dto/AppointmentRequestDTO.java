package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    @NotNull(message = "Patient ID cannot be null")
    private UUID patientId;

    @NotNull(message = "Doctor ID cannot be null")
    private UUID doctorId;

    @NotNull(message = "Clinic ID cannot be null")
    private UUID clinicId;

    @NotNull(message = "Appointment date cannot be null")
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time cannot be null")
    private LocalTime appointmentTime;

    @NotBlank(message = "Reason for appointment cannot be blank")
    @Size(max = 500, message = "Reason too long")
    private String reason;

    // Status is usually set by the system (e.g., "Scheduled") not by the client in a request
    // private String status;
}