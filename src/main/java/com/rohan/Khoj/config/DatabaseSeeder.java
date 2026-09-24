package com.rohan.Khoj.config;

import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.clinic.ClinicRepository;
import com.rohan.Khoj.common.Role;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.doctor.DoctorRepository;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import com.rohan.Khoj.appointment.AppointmentDetailEntity;
import com.rohan.Khoj.appointment.AppointmentRepository;
import com.rohan.Khoj.vital.VitalEntity;
import com.rohan.Khoj.vital.VitalRepository;
import com.rohan.Khoj.prescription.PrescriptionEntity;
import com.rohan.Khoj.prescription.PrescriptionRepository;
import com.rohan.Khoj.notification.NotificationEntity;
import com.rohan.Khoj.notification.NotificationRepository;
import com.rohan.Khoj.healthrecord.HealthRecordEntity;
import com.rohan.Khoj.healthrecord.HealthRecordRepository;
import com.rohan.Khoj.common.Gender;
import com.rohan.Khoj.notification.NotificationType;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationRepository;
import com.rohan.Khoj.affiliation.AffiliationStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentRepository appointmentRepository;
    private final VitalRepository vitalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final NotificationRepository notificationRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final DoctorClinicAffiliationRepository affiliationRepository;
    
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        PatientEntity patient = seedPatient();
        DoctorEntity doctor = seedDoctor();
        ClinicEntity clinic = seedClinic();
        
        seedDashboardData(patient, doctor, clinic);
    }

    private PatientEntity seedPatient() {
        return patientRepository.findByUsername("patient").orElseGet(() -> {
            PatientEntity patient = PatientEntity.builder()
                    .username("patient")
                    .password(passwordEncoder.encode("Password@123"))
                    .emailId("patient@khoj.com")
                    .role(Role.ROLE_PATIENT)
                    .firstName("Test")
                    .lastName("Patient")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .gender(Gender.MALE)
                    .primaryMobile("9876543210")
                    .bloodGroup("O+")
                    .build();
            patientRepository.save(patient);
            System.out.println("Seeded test patient: patient / Password@123");
            return patient;
        });
    }

    private DoctorEntity seedDoctor() {
        return doctorRepository.findByUsername("doctor").orElseGet(() -> {
            DoctorEntity doctor = DoctorEntity.builder()
                    .username("doctor")
                    .password(passwordEncoder.encode("Password@123"))
                    .emailId("doctor@khoj.com")
                    .role(Role.ROLE_DOCTOR)
                    .firstName("Test")
                    .lastName("Doctor")
                    .gender(Gender.FEMALE)
                    .primaryMobile("8765432109")
                    .registrationNumber("MCI-12345")
                    .registrationIssueDate(LocalDate.of(2015, 5, 10))
                    .specializations("Cardiologist")
                    .qualifications("MBBS, MD")
                    .build();
            doctorRepository.save(doctor);
            System.out.println("Seeded test doctor: doctor / Password@123");
            return doctor;
        });
    }

    private ClinicEntity seedClinic() {
        return clinicRepository.findByUsername("clinic").orElseGet(() -> {
            ClinicEntity clinic = ClinicEntity.builder()
                    .username("clinic")
                    .password(passwordEncoder.encode("Password@123"))
                    .emailId("clinic@khoj.com")
                    .role(Role.ROLE_CLINIC)
                    .name("Khoj Test Clinic")
                    .primaryMobile("7654321098")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .build();
            clinicRepository.save(clinic);
            System.out.println("Seeded test clinic: clinic / Password@123");
            return clinic;
        });
    }
    
    private void seedDashboardData(PatientEntity patient, DoctorEntity doctor, ClinicEntity clinic) {
        DoctorClinicAffiliationEntity affiliation;
        if (affiliationRepository.count() == 0) {
            affiliation = affiliationRepository.save(DoctorClinicAffiliationEntity.builder()
                .doctor(doctor)
                .clinic(clinic)
                .status(AffiliationStatus.APPROVED)
                .dailyPatientLimit(20)
                .initiatedBy(com.rohan.Khoj.affiliation.AffiliationRequestInitiator.DOCTOR)
                .actionRequiredBy(com.rohan.Khoj.affiliation.AffiliationActionRequiredBy.CLINIC)
                .build());
        } else {
            affiliation = affiliationRepository.findAll().get(0);
        }

        // Appointments
        if (appointmentRepository.count() == 0) {
            appointmentRepository.save(AppointmentDetailEntity.builder()
                    .patient(patient)
                    .affiliation(affiliation)
                    .appointmentDate(LocalDate.now().plusDays(2))
                    .appointmentTime(LocalTime.of(10, 0))
                    .appointmentTimeSlot("MONDAY_10:00") // Approximation for slot string
                    .status("SCHEDULED")
                    .reason("Routine Checkup")
                    .build());
            System.out.println("Seeded test appointment");
        }

        // Vitals
        if (vitalRepository.count() == 0) {
            vitalRepository.save(VitalEntity.builder()
                    .patient(patient)
                    .systolicBp(120)
                    .diastolicBp(80)
                    .heartRate(72)
                    .weight(75.5)
                    .temperature(98.6)
                    .build());
            System.out.println("Seeded test vitals");
        }

        // Prescriptions
        if (prescriptionRepository.count() == 0) {
            prescriptionRepository.save(PrescriptionEntity.builder()
                    .patient(patient)
                    .doctor(doctor)
                    .isActive(true)
                    .build());
            System.out.println("Seeded test prescription");
        }

        // Notifications
        if (notificationRepository.count() == 0) {
            notificationRepository.save(NotificationEntity.builder()
                    .userId(patient.getId())
                    .title("Upcoming Appointment")
                    .message("You have an appointment in 2 days with Dr. " + doctor.getLastName())
                    .type(NotificationType.INFO)
                    .isRead(false)
                    .build());
            System.out.println("Seeded test notification");
        }

        // Health Records
        if (healthRecordRepository.count() == 0) {
            healthRecordRepository.save(HealthRecordEntity.builder()
                    .patient(patient)
                    .documentTitle("Complete Blood Count (CBC)")
                    .documentType("LAB_REPORT")
                    .documentUrl("https://example.com/reports/cbc.pdf")
                    .build());
            System.out.println("Seeded test health record");
        }
    }
}
