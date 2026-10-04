package com.rohan.Khoj.doctor;

import com.rohan.Khoj.affiliation.AffiliationResponseDTO;
import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.patient.PatientDTO;
import com.rohan.Khoj.prescription.PrescriptionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDashboardDTO {

    // Doctor profile details
    private DoctorDTO profile;

    // Summary statistics / metrics
    private long totalPatients;
    private long todayAppointmentsCount;
    private long totalAppointments;
    private long activeAffiliationsCount;
    private long pendingAffiliationsCount;
    private long totalPrescriptionsIssued;

    // Recent activity & lists
    private List<AppointmentDTO> todayAppointments;
    private List<AppointmentDTO> upcomingAppointments;
    private List<AffiliationResponseDTO> activeAffiliations;
    private List<AffiliationResponseDTO> pendingAffiliations;
    private List<PatientDTO> recentPatients;
    private List<PrescriptionDTO> recentPrescriptions;
}
