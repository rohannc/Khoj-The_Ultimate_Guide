package com.rohan.Khoj.appointment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusUpdateDTO {

    @NotNull(message = "Appointment ID cannot be null")
    private UUID appointmentId;

    @NotNull(message = "Status cannot be null")
    private String status;
}
