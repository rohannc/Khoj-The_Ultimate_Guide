package com.rohan.Khoj.clinic;

import com.rohan.Khoj.affiliation.AffiliationResponseDTO;
import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.doctor.DoctorDTO;
import com.rohan.Khoj.patient.PatientDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDashboardDTO {

    // Clinic profile details
    private ClinicDTO profile;

    // Summary statistics / KPIs
    private long totalPatients;
    private long todayAppointmentsCount;
    private long totalAppointments;
    private long activeDoctorsCount;
    private long pendingAffiliationsCount;

    // Operational lists
    private List<AppointmentDTO> todayAppointments;
    private List<AppointmentDTO> upcomingAppointments;
    private List<AffiliationResponseDTO> activeAffiliations;
    private List<AffiliationResponseDTO> pendingAffiliations;
    private List<DoctorDTO> affiliatedDoctors;
    private List<PatientDTO> recentPatients;
}
