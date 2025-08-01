package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
// import java.util.Optional; // Use Optional for fields that truly might not be present and should not affect update

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequestDTO {
    // PatientId, DoctorId, ClinicId are typically not updated directly in an appointment,
    // as changing them usually implies cancelling and rescheduling a new appointment.
    // If they can be changed, they should be present here as Optional<UUID>
    // and handled in the service logic for fetching/setting new entities.
    // private Optional<UUID> patientId;
    // private Optional<UUID> doctorId;
    // private Optional<UUID> clinicId;

    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate; // Optional update

    private LocalTime appointmentTime; // Optional update

    @Size(max = 500, message = "Reason too long")
    private String reason; // Optional update

    // @NotBlank(message = "Status cannot be blank")
    // @Pattern(regexp = "Scheduled|Confirmed|Cancelled|Completed|Rescheduled", message = "Invalid appointment status")
    // private String status; // Status is commonly updated
}