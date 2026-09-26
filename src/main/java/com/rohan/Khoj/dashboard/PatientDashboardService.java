package com.rohan.Khoj.dashboard;

import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.appointment.AppointmentRepository;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.healthrecord.HealthRecordDTO;
import com.rohan.Khoj.healthrecord.HealthRecordRepository;
import com.rohan.Khoj.notification.NotificationDTO;
import com.rohan.Khoj.notification.NotificationRepository;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import com.rohan.Khoj.prescription.PrescriptionDTO;
import com.rohan.Khoj.prescription.PrescriptionRepository;
import com.rohan.Khoj.vital.VitalDTO;
import com.rohan.Khoj.vital.VitalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientDashboardService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final VitalRepository vitalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public PatientDashboardDTO getDashboardData(UUID patientId) {
        // 1. Fetch Patient Demographics
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // 2. Fetch the Immediate Upcoming Appointment (Top 1 SCHEDULED)
        AppointmentDTO immediateAppointment = appointmentRepository
                .findByPatientIdAndStatusOrderByAppointmentDateAsc(patientId, "SCHEDULED", PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(entity -> modelMapper.map(entity, AppointmentDTO.class))
                .orElse(null);

        // 3. Fetch All Recent Vitals (ordered by recordedAt desc)
        List<VitalDTO> recentVitals = vitalRepository
                .findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream()
                .map(entity -> modelMapper.map(entity, VitalDTO.class))
                .collect(Collectors.toList());

        // 4. Fetch Two Active Prescriptions
        List<PrescriptionDTO> activePrescriptions = prescriptionRepository
                .findByPatientIdAndIsActiveTrueOrderByIssuedAtDesc(patientId)
                .stream()
                .limit(2)
                .map(entity -> modelMapper.map(entity, PrescriptionDTO.class))
                .collect(Collectors.toList());

        // 5. Fetch Two Recent Health Records
        List<HealthRecordDTO> recentHealthRecords = healthRecordRepository
                .findByPatientIdOrderByUploadedAtDesc(patientId, PageRequest.of(0, 2))
                .stream()
                .map(entity -> modelMapper.map(entity, HealthRecordDTO.class))
                .collect(Collectors.toList());

        // 6. Fetch Latest 3 Unread Notifications (full list available via /api/notifications)
        List<NotificationDTO> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(patientId, PageRequest.of(0, 3))
                .stream()
                .map(entity -> modelMapper.map(entity, NotificationDTO.class))
                .collect(Collectors.toList());

        return PatientDashboardDTO.builder()
                .patientId(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .primaryMobile(patient.getPrimaryMobile())
                .emailId(patient.getEmailId())
                .immediateAppointment(immediateAppointment)
                .recentVitals(recentVitals)
                .activePrescriptions(activePrescriptions)
                .recentHealthRecords(recentHealthRecords)
                .unreadNotifications(unreadNotifications)
                .build();
    }
}
