package com.rohan.Khoj.dashboard;

import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.healthrecord.HealthRecordDTO;
import com.rohan.Khoj.notification.NotificationDTO;
import com.rohan.Khoj.prescription.PrescriptionDTO;
import com.rohan.Khoj.vital.VitalDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PatientDashboardDTO {
    private List<AppointmentDTO> upcomingAppointments;
    private List<VitalDTO> recentVitals;
    private List<PrescriptionDTO> activePrescriptions;
    private List<NotificationDTO> unreadNotifications;
}
