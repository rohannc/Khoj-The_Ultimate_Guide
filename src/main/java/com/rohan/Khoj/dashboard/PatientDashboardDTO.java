package com.rohan.Khoj.dashboard;

import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.common.Gender;
import com.rohan.Khoj.healthrecord.HealthRecordDTO;
import com.rohan.Khoj.notification.NotificationDTO;
import com.rohan.Khoj.prescription.PrescriptionDTO;
import com.rohan.Khoj.vital.VitalDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Aggregated patient dashboard summary response")
public class PatientDashboardDTO {

    @Schema(description = "Unique identifier of the patient", example = "0b1cd526-508f-4b5d-8540-6991925669a1")
    private UUID patientId;

    @Schema(description = "First name of the patient", example = "John")
    private String firstName;

    @Schema(description = "Last name of the patient", example = "Doe")
    private String lastName;

    @Schema(description = "Date of birth of the patient", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender of the patient", example = "MALE")
    private Gender gender;

    @Schema(description = "Primary contact mobile number", example = "9876543210")
    private String primaryMobile;

    @Schema(description = "Registered email address", example = "john.doe@example.com")
    private String emailId;

    @Schema(description = "Immediate next scheduled appointment, or null if no upcoming appointment exists")
    private AppointmentDTO immediateAppointment;

    @Schema(description = "List of all recent recorded vitals ordered chronologically descending")
    private List<VitalDTO> recentVitals;

    @Schema(description = "Up to two currently active prescriptions ordered by latest issue date")
    private List<PrescriptionDTO> activePrescriptions;

    @Schema(description = "Up to two recently uploaded health records / documents")
    private List<HealthRecordDTO> recentHealthRecords;

    @Schema(description = "Unread notifications for the patient (top 5)")
    private List<NotificationDTO> unreadNotifications;

    /**
     * Backward-compatibility helper for UI components expecting 'upcomingAppointments' as a list.
     */
    public List<AppointmentDTO> getUpcomingAppointments() {
        return immediateAppointment != null ? List.of(immediateAppointment) : List.of();
    }
}
