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
import com.rohan.Khoj.prescription.PrescriptionItemEntity;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    // Static UUID constants for deterministic mock data
    public static final UUID STATIC_PATIENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID STATIC_DOCTOR_ID  = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID STATIC_CLINIC_ID  = UUID.fromString("00000000-0000-0000-0000-000000000003");
    public static final UUID STATIC_AFFILIATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000004");

    public static final UUID STATIC_APPT_1_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    public static final UUID STATIC_APPT_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    public static final UUID STATIC_APPT_3_ID = UUID.fromString("00000000-0000-0000-0000-000000000013");

    public static final UUID STATIC_VITAL_1_ID = UUID.fromString("00000000-0000-0000-0000-000000000021");
    public static final UUID STATIC_VITAL_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000022");
    public static final UUID STATIC_VITAL_3_ID = UUID.fromString("00000000-0000-0000-0000-000000000023");
    public static final UUID STATIC_VITAL_4_ID = UUID.fromString("00000000-0000-0000-0000-000000000024");

    public static final UUID STATIC_RX_1_ID = UUID.fromString("00000000-0000-0000-0000-000000000031");
    public static final UUID STATIC_RX_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000032");
    public static final UUID STATIC_RX_3_ID = UUID.fromString("00000000-0000-0000-0000-000000000033");
    public static final UUID STATIC_RX_4_ID = UUID.fromString("00000000-0000-0000-0000-000000000034");

    public static final UUID STATIC_RX_ITEM_1_ID = UUID.fromString("00000000-0000-0000-0000-000000000041");
    public static final UUID STATIC_RX_ITEM_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000042");
    public static final UUID STATIC_RX_ITEM_3_ID = UUID.fromString("00000000-0000-0000-0000-000000000043");
    public static final UUID STATIC_RX_ITEM_4_ID = UUID.fromString("00000000-0000-0000-0000-000000000044");

    public static final UUID STATIC_NOTIF_1_ID = UUID.fromString("00000000-0000-0000-0000-000000000051");
    public static final UUID STATIC_NOTIF_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000052");
    public static final UUID STATIC_NOTIF_3_ID = UUID.fromString("00000000-0000-0000-0000-000000000053");
    public static final UUID STATIC_NOTIF_4_ID = UUID.fromString("00000000-0000-0000-0000-000000000054");

    public static final UUID STATIC_RECORD_1_ID = UUID.fromString("00000000-0000-0000-0000-000000000061");
    public static final UUID STATIC_RECORD_2_ID = UUID.fromString("00000000-0000-0000-0000-000000000062");
    public static final UUID STATIC_RECORD_3_ID = UUID.fromString("00000000-0000-0000-0000-000000000063");
    public static final UUID STATIC_RECORD_4_ID = UUID.fromString("00000000-0000-0000-0000-000000000064");
    public static final UUID STATIC_RECORD_5_ID = UUID.fromString("00000000-0000-0000-0000-000000000065");

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
        PatientEntity patient2 = seedPatient2();
        PatientEntity patient3 = seedPatient3();
        
        DoctorEntity doctor = seedDoctor();
        DoctorEntity doctor2 = seedDoctor2();
        
        ClinicEntity clinic = seedClinic();
        ClinicEntity clinic2 = seedClinic2();
        
        patientRepository.flush();
        doctorRepository.flush();
        clinicRepository.flush();
        
        seedDashboardData(patient, doctor, clinic);
        seedExtraData(patient2, patient3, doctor, doctor2, clinic, clinic2);
    }

    private PatientEntity seedPatient2() {
        return patientRepository.findByUsername("patient2").orElseGet(() -> {
            PatientEntity p = PatientEntity.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000101"))
                    .username("patient2").password(passwordEncoder.encode("Password@123")).emailId("patient2@khoj.com")
                    .role(Role.ROLE_PATIENT).firstName("Alice").lastName("Smith").dateOfBirth(LocalDate.of(1992, 2, 2))
                    .gender(Gender.FEMALE).primaryMobile("9876543211").bloodGroup("A+")
                    .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0)).build();
            return patientRepository.save(p);
        });
    }

    private PatientEntity seedPatient3() {
        return patientRepository.findByUsername("patient3").orElseGet(() -> {
            PatientEntity p = PatientEntity.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000102"))
                    .username("patient3").password(passwordEncoder.encode("Password@123")).emailId("patient3@khoj.com")
                    .role(Role.ROLE_PATIENT).firstName("Bob").lastName("Jones").dateOfBirth(LocalDate.of(1985, 3, 3))
                    .gender(Gender.MALE).primaryMobile("9876543212").bloodGroup("B+")
                    .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0)).build();
            return patientRepository.save(p);
        });
    }

    private DoctorEntity seedDoctor2() {
        return doctorRepository.findByUsername("doctor2").orElseGet(() -> {
            DoctorEntity d = DoctorEntity.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000201"))
                    .username("doctor2").password(passwordEncoder.encode("Password@123")).emailId("doctor2@khoj.com")
                    .role(Role.ROLE_DOCTOR).firstName("Jane").lastName("Doe").gender(Gender.FEMALE).primaryMobile("8765432101")
                    .registrationNumber("MCI-54321").registrationIssueDate(LocalDate.of(2018, 5, 10))
                    .specializations("Neurologist").qualifications("MBBS, DM").createdAt(LocalDateTime.of(2025, 1, 1, 9, 0)).build();
            return doctorRepository.save(d);
        });
    }

    private ClinicEntity seedClinic2() {
        return clinicRepository.findByUsername("clinic2").orElseGet(() -> {
            ClinicEntity c = ClinicEntity.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000301"))
                    .username("clinic2").password(passwordEncoder.encode("Password@123")).emailId("clinic2@khoj.com")
                    .role(Role.ROLE_CLINIC).name("City Care Clinic").primaryMobile("7654321099")
                    .city("Pune").state("Maharashtra").createdAt(LocalDateTime.of(2025, 1, 1, 9, 0)).build();
            return clinicRepository.save(c);
        });
    }

    private void seedExtraData(PatientEntity p2, PatientEntity p3, DoctorEntity d1, DoctorEntity d2, ClinicEntity c1, ClinicEntity c2) {
        if (affiliationRepository.count() < 3) {
            DoctorClinicAffiliationEntity aff2 = affiliationRepository.save(DoctorClinicAffiliationEntity.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000401"))
                .version(0L).doctor(d2).clinic(c1).status(AffiliationStatus.APPROVED).dailyPatientLimit(15)
                .initiatedBy(com.rohan.Khoj.affiliation.AffiliationRequestInitiator.CLINIC)
                .actionRequiredBy(com.rohan.Khoj.affiliation.AffiliationActionRequiredBy.DOCTOR)
                .joiningDate(LocalDate.of(2025, 2, 1)).doctorCharge(600.0).clinicCharge(150.0)
                .mondayStart(LocalTime.of(10, 0)).mondayEnd(LocalTime.of(14, 0))
                .build());

            DoctorClinicAffiliationEntity aff3 = affiliationRepository.save(DoctorClinicAffiliationEntity.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000402"))
                .version(0L).doctor(d1).clinic(c2).status(AffiliationStatus.APPROVED).dailyPatientLimit(25)
                .initiatedBy(com.rohan.Khoj.affiliation.AffiliationRequestInitiator.DOCTOR)
                .actionRequiredBy(com.rohan.Khoj.affiliation.AffiliationActionRequiredBy.CLINIC)
                .joiningDate(LocalDate.of(2025, 3, 1)).doctorCharge(400.0).clinicCharge(100.0)
                .tuesdayStart(LocalTime.of(9, 0)).tuesdayEnd(LocalTime.of(17, 0))
                .build());

            // Add appointments
            appointmentRepository.save(AppointmentDetailEntity.builder()
                    .id(UUID.randomUUID()).version(0L).patient(p2).affiliation(aff2)
                    .appointmentDate(LocalDate.now().plusDays(1)).appointmentTime(LocalTime.of(11, 0))
                    .tokenNumber(1).status("SCHEDULED").reason("Headache")
                    .build());
            
            appointmentRepository.save(AppointmentDetailEntity.builder()
                    .id(UUID.randomUUID()).version(0L).patient(p3).affiliation(aff3)
                    .appointmentDate(LocalDate.now().plusDays(2)).appointmentTime(LocalTime.of(15, 0))
                    .tokenNumber(2).status("SCHEDULED").reason("Routine Checkup")
                    .build());
            
            // Add vitals
            vitalRepository.save(VitalEntity.builder().id(UUID.randomUUID()).patient(p2).systolicBp(110).diastolicBp(70).heartRate(65).build());
        }
    }

    private PatientEntity seedPatient() {
        return patientRepository.findByUsername("patient").orElseGet(() -> {
            PatientEntity patient = PatientEntity.builder()
                    .id(STATIC_PATIENT_ID)
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
                    .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                    .build();
            PatientEntity saved = patientRepository.save(patient);
            System.out.println("Seeded test patient with static ID: " + saved.getId());
            return saved;
        });
    }

    private DoctorEntity seedDoctor() {
        return doctorRepository.findByUsername("doctor").orElseGet(() -> {
            DoctorEntity doctor = DoctorEntity.builder()
                    .id(STATIC_DOCTOR_ID)
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
                    .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                    .build();
            DoctorEntity saved = doctorRepository.save(doctor);
            System.out.println("Seeded test doctor with static ID: " + saved.getId());
            return saved;
        });
    }

    private ClinicEntity seedClinic() {
        return clinicRepository.findByUsername("clinic").orElseGet(() -> {
            ClinicEntity clinic = ClinicEntity.builder()
                    .id(STATIC_CLINIC_ID)
                    .username("clinic")
                    .password(passwordEncoder.encode("Password@123"))
                    .emailId("clinic@khoj.com")
                    .role(Role.ROLE_CLINIC)
                    .name("Khoj Test Clinic")
                    .primaryMobile("7654321098")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .createdAt(LocalDateTime.of(2025, 1, 1, 9, 0))
                    .build();
            ClinicEntity saved = clinicRepository.save(clinic);
            System.out.println("Seeded test clinic with static ID: " + saved.getId());
            return saved;
        });
    }
    
    private void seedDashboardData(PatientEntity patient, DoctorEntity doctor, ClinicEntity clinic) {
        DoctorClinicAffiliationEntity affiliation;
        if (affiliationRepository.count() == 0) {
            affiliation = affiliationRepository.save(DoctorClinicAffiliationEntity.builder()
                .id(STATIC_AFFILIATION_ID)
                .version(0L)
                .doctor(doctor)
                .clinic(clinic)
                .status(AffiliationStatus.APPROVED)
                .dailyPatientLimit(20)
                .initiatedBy(com.rohan.Khoj.affiliation.AffiliationRequestInitiator.DOCTOR)
                .actionRequiredBy(com.rohan.Khoj.affiliation.AffiliationActionRequiredBy.CLINIC)
                .joiningDate(LocalDate.of(2025, 1, 15))
                .doctorCharge(500.0)
                .clinicCharge(100.0)
                .mondayStart(LocalTime.of(9, 0)).mondayEnd(LocalTime.of(17, 0))
                .tuesdayStart(LocalTime.of(9, 0)).tuesdayEnd(LocalTime.of(17, 0))
                .wednesdayStart(LocalTime.of(9, 0)).wednesdayEnd(LocalTime.of(17, 0))
                .thursdayStart(LocalTime.of(9, 0)).thursdayEnd(LocalTime.of(17, 0))
                .fridayStart(LocalTime.of(9, 0)).fridayEnd(LocalTime.of(17, 0))
                .saturdayStart(LocalTime.of(10, 0)).saturdayEnd(LocalTime.of(14, 0))
                .build());
        } else {
            affiliation = affiliationRepository.findAll().get(0);
        }

        // Appointments - Static IDs, Dates & Times
        if (appointmentRepository.count() == 0) {
            appointmentRepository.save(AppointmentDetailEntity.builder()
                    .id(STATIC_APPT_1_ID)
                    .version(0L)
                    .patient(patient)
                    .affiliation(affiliation)
                    .appointmentDate(LocalDate.of(2026, 9, 28))
                    .appointmentTime(LocalTime.of(10, 0))
                    .tokenNumber(5)
                    .status("SCHEDULED")
                    .reason("Follow-up Consultation for Hypertension")
                    .build());
                    
            appointmentRepository.save(AppointmentDetailEntity.builder()
                    .id(STATIC_APPT_2_ID)
                    .version(0L)
                    .patient(patient)
                    .affiliation(affiliation)
                    .appointmentDate(LocalDate.of(2026, 10, 10))
                    .appointmentTime(LocalTime.of(14, 30))
                    .tokenNumber(12)
                    .status("SCHEDULED")
                    .reason("Cardiology Stress Test & Review")
                    .build());

            appointmentRepository.save(AppointmentDetailEntity.builder()
                    .id(STATIC_APPT_3_ID)
                    .version(0L)
                    .patient(patient)
                    .affiliation(affiliation)
                    .appointmentDate(LocalDate.of(2026, 9, 21))
                    .appointmentTime(LocalTime.of(11, 30))
                    .tokenNumber(8)
                    .status("COMPLETED")
                    .reason("Seasonal Viral Fever & Cough")
                    .build());
            System.out.println("Seeded static test appointments");
        }

        // Vitals History - Static IDs & Measurements
        if (vitalRepository.count() == 0) {
            vitalRepository.save(VitalEntity.builder()
                    .id(STATIC_VITAL_1_ID)
                    .patient(patient)
                    .systolicBp(118)
                    .diastolicBp(78)
                    .heartRate(70)
                    .weight(74.8)
                    .temperature(98.4)
                    .heightCm(175.5)
                    .bmi(24.3)
                    .build());

            vitalRepository.save(VitalEntity.builder()
                    .id(STATIC_VITAL_2_ID)
                    .patient(patient)
                    .systolicBp(125)
                    .diastolicBp(82)
                    .heartRate(80)
                    .weight(76.0)
                    .temperature(99.1)
                    .heightCm(175.5)
                    .bmi(24.7)
                    .build());

            vitalRepository.save(VitalEntity.builder()
                    .id(STATIC_VITAL_3_ID)
                    .patient(patient)
                    .systolicBp(120)
                    .diastolicBp(80)
                    .heartRate(72)
                    .weight(75.5)
                    .temperature(98.6)
                    .heightCm(175.5)
                    .bmi(24.5)
                    .build());

            vitalRepository.save(VitalEntity.builder()
                    .id(STATIC_VITAL_4_ID)
                    .patient(patient)
                    .systolicBp(130)
                    .diastolicBp(85)
                    .heartRate(84)
                    .weight(76.8)
                    .temperature(98.7)
                    .heightCm(175.5)
                    .bmi(24.9)
                    .build());
            System.out.println("Seeded static test vitals");
        }

        // Prescriptions & Medications - Static IDs & Details
        if (prescriptionRepository.count() == 0) {
            // Prescription 1: Active - Blood Pressure & Heart Health
            PrescriptionEntity rx1 = PrescriptionEntity.builder()
                    .id(STATIC_RX_1_ID)
                    .patient(patient)
                    .doctor(doctor)
                    .isActive(true)
                    .diagnosis("Mild Essential Hypertension")
                    .notes("Take after breakfast with water. Avoid excess salt intake.")
                    .build();
            PrescriptionItemEntity item1 = PrescriptionItemEntity.builder()
                    .id(STATIC_RX_ITEM_1_ID)
                    .prescription(rx1)
                    .medicationName("Amlodipine Besylate 5mg")
                    .dosage("5mg")
                    .frequency("Once daily (Morning)")
                    .startedAt(LocalDate.of(2026, 9, 20))
                    .durationValue(1)
                    .durationUnit(com.rohan.Khoj.prescription.DurationUnit.MONTH)
                    .instructions("Take after breakfast with water. Avoid excess salt intake.")
                    .isActive(true)
                    .build();
            rx1.getItems().add(item1);
            prescriptionRepository.save(rx1);

            // Prescription 2: Active - Daily Vitamin Supplement
            PrescriptionEntity rx2 = PrescriptionEntity.builder()
                    .id(STATIC_RX_2_ID)
                    .patient(patient)
                    .doctor(doctor)
                    .isActive(true)
                    .diagnosis("Vitamin D & Calcium Deficiency")
                    .notes("Take after dinner with milk.")
                    .build();
            PrescriptionItemEntity item2 = PrescriptionItemEntity.builder()
                    .id(STATIC_RX_ITEM_2_ID)
                    .prescription(rx2)
                    .medicationName("Cholecalciferol (Vitamin D3) 60,000 IU")
                    .dosage("60,000 IU")
                    .frequency("Once weekly")
                    .startedAt(LocalDate.of(2026, 9, 15))
                    .durationValue(2)
                    .durationUnit(com.rohan.Khoj.prescription.DurationUnit.MONTH)
                    .instructions("Take after dinner with milk.")
                    .isActive(true)
                    .build();
            rx2.getItems().add(item2);
            prescriptionRepository.save(rx2);

            // Prescription 3: Inactive / Past - Acute Viral Fever
            PrescriptionEntity rx3 = PrescriptionEntity.builder()
                    .id(STATIC_RX_3_ID)
                    .patient(patient)
                    .doctor(doctor)
                    .isActive(false)
                    .diagnosis("Acute Viral Fever & Sore Throat")
                    .notes("Completed course. Drink plenty of fluids.")
                    .build();
            PrescriptionItemEntity item3 = PrescriptionItemEntity.builder()
                    .id(STATIC_RX_ITEM_3_ID)
                    .prescription(rx3)
                    .medicationName("Paracetamol 650mg")
                    .dosage("650mg")
                    .frequency("Twice daily after meals")
                    .startedAt(LocalDate.of(2026, 9, 10))
                    .durationValue(5)
                    .durationUnit(com.rohan.Khoj.prescription.DurationUnit.DAY)
                    .instructions("Completed course. Drink plenty of fluids.")
                    .isActive(false)
                    .discontinueReason("Completed treatment course")
                    .build();
            rx3.getItems().add(item3);
            prescriptionRepository.save(rx3);

            // Prescription 4: Inactive / Past - Allergy Relief
            PrescriptionEntity rx4 = PrescriptionEntity.builder()
                    .id(STATIC_RX_4_ID)
                    .patient(patient)
                    .doctor(doctor)
                    .isActive(false)
                    .diagnosis("Allergic Rhinitis")
                    .notes("Take at bedtime if sneezing occurs.")
                    .build();
            PrescriptionItemEntity item4 = PrescriptionItemEntity.builder()
                    .id(STATIC_RX_ITEM_4_ID)
                    .prescription(rx4)
                    .medicationName("Cetirizine Hydrochloride 10mg")
                    .dosage("10mg")
                    .frequency("Once at night")
                    .startedAt(LocalDate.of(2026, 8, 1))
                    .durationValue(1)
                    .durationUnit(com.rohan.Khoj.prescription.DurationUnit.WEEK)
                    .instructions("Take at bedtime if sneezing occurs.")
                    .isActive(false)
                    .discontinueReason("Symptoms resolved")
                    .build();
            rx4.getItems().add(item4);
            prescriptionRepository.save(rx4);

            System.out.println("Seeded static test prescriptions with items");
        }

        // Notifications - Static IDs & Messages
        if (notificationRepository.count() == 0) {
            notificationRepository.save(NotificationEntity.builder()
                    .id(STATIC_NOTIF_1_ID)
                    .userId(patient.getId())
                    .title("Upcoming Appointment Reminder")
                    .message("Your appointment with Dr. " + doctor.getLastName() + " is scheduled for Monday at 10:00 AM.")
                    .type(NotificationType.APPOINTMENT_REMINDER)
                    .isRead(false)
                    .build());
                    
            notificationRepository.save(NotificationEntity.builder()
                    .id(STATIC_NOTIF_2_ID)
                    .userId(patient.getId())
                    .title("New Lab Report Ready")
                    .message("Your Lipid Profile panel report has been analyzed and is available to download.")
                    .type(NotificationType.INFO)
                    .isRead(false)
                    .build());

            notificationRepository.save(NotificationEntity.builder()
                    .id(STATIC_NOTIF_3_ID)
                    .userId(patient.getId())
                    .title("Prescription Refill Reminder")
                    .message("Your Amlodipine 5mg prescription is due for review in 5 days.")
                    .type(NotificationType.ACTION_REQUIRED)
                    .isRead(false)
                    .build());

            notificationRepository.save(NotificationEntity.builder()
                    .id(STATIC_NOTIF_4_ID)
                    .userId(patient.getId())
                    .title("Doctor Note Added")
                    .message("Dr. " + doctor.getLastName() + " updated your follow-up instructions.")
                    .type(NotificationType.INFO)
                    .isRead(true)
                    .build());
            System.out.println("Seeded static test notifications");
        }

        // Health Records & Reports - Static IDs & Static Dates
        if (healthRecordRepository.count() == 0) {
            healthRecordRepository.save(HealthRecordEntity.builder()
                    .id(STATIC_RECORD_1_ID)
                    .patient(patient)
                    .documentTitle("Comprehensive Metabolic Panel & Lipid Profile")
                    .documentType("LAB_REPORT")
                    .documentUrl("https://example.com/reports/lipid_profile.pdf")
                    .testDate(LocalDate.of(2026, 9, 23))
                    .build());

            healthRecordRepository.save(HealthRecordEntity.builder()
                    .id(STATIC_RECORD_2_ID)
                    .patient(patient)
                    .documentTitle("Complete Blood Count (CBC) with Differential")
                    .documentType("LAB_REPORT")
                    .documentUrl("https://example.com/reports/cbc_diff.pdf")
                    .testDate(LocalDate.of(2026, 9, 11))
                    .build());

            healthRecordRepository.save(HealthRecordEntity.builder()
                    .id(STATIC_RECORD_3_ID)
                    .patient(patient)
                    .documentTitle("Digital Chest X-Ray (PA View)")
                    .documentType("IMAGING")
                    .documentUrl("https://example.com/reports/chest_xray.pdf")
                    .testDate(LocalDate.of(2026, 8, 26))
                    .build());

            healthRecordRepository.save(HealthRecordEntity.builder()
                    .id(STATIC_RECORD_4_ID)
                    .patient(patient)
                    .documentTitle("Electrocardiogram (12-Lead ECG)")
                    .documentType("DIAGNOSTIC")
                    .documentUrl("https://example.com/reports/ecg_report.pdf")
                    .testDate(LocalDate.of(2026, 7, 26))
                    .build());

            healthRecordRepository.save(HealthRecordEntity.builder()
                    .id(STATIC_RECORD_5_ID)
                    .patient(patient)
                    .documentTitle("Abdominal Ultrasound Scan")
                    .documentType("IMAGING")
                    .documentUrl("https://example.com/reports/usg_abdomen.pdf")
                    .testDate(LocalDate.of(2026, 5, 26))
                    .build());
            System.out.println("Seeded static test health records");
        }
    }
}

