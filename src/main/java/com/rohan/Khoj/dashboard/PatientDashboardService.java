package com.rohan.Khoj.dashboard;

import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.appointment.AppointmentRepository;
import com.rohan.Khoj.notification.NotificationDTO;
import com.rohan.Khoj.notification.NotificationRepository;
import com.rohan.Khoj.prescription.PrescriptionDTO;
import com.rohan.Khoj.prescription.PrescriptionRepository;
import com.rohan.Khoj.vital.VitalDTO;
import com.rohan.Khoj.vital.VitalRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientDashboardService {

    private final AppointmentRepository appointmentRepository;
    private final VitalRepository vitalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public PatientDashboardDTO getDashboardData(UUID patientId) {
        
        // 1. Fetch Top 3 Upcoming Appointments
        List<AppointmentDTO> upcomingAppointments = appointmentRepository
                .findByPatientIdAndStatusOrderByAppointmentDateAsc(patientId, "SCHEDULED", PageRequest.of(0, 3))
                .stream()
                .map(entity -> modelMapper.map(entity, AppointmentDTO.class))
                .collect(Collectors.toList());

        // 2. Fetch Top 3 Recent Vitals
        List<VitalDTO> recentVitals = vitalRepository
                .findByPatientIdOrderByRecordedAtDesc(patientId, PageRequest.of(0, 3))
                .stream()
                .map(entity -> modelMapper.map(entity, VitalDTO.class))
                .collect(Collectors.toList());

        // 3. Fetch Active Prescriptions
        List<PrescriptionDTO> activePrescriptions = prescriptionRepository
                .findByPatientIdAndIsActiveTrueOrderByIssuedAtDesc(patientId)
                .stream()
                .map(entity -> modelMapper.map(entity, PrescriptionDTO.class))
                .collect(Collectors.toList());

        // 4. Fetch Top 5 Unread Notifications
        List<NotificationDTO> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(patientId, PageRequest.of(0, 5))
                .stream()
                .map(entity -> modelMapper.map(entity, NotificationDTO.class))
                .collect(Collectors.toList());

        return PatientDashboardDTO.builder()
                .upcomingAppointments(upcomingAppointments)
                .recentVitals(recentVitals)
                .activePrescriptions(activePrescriptions)
                .unreadNotifications(unreadNotifications)
                .build();
    }
}
