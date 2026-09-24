package com.rohan.Khoj.appointment;

import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.appointment.AppointmentDTO;
import com.rohan.Khoj.appointment.AppointmentRequestDTO;
import com.rohan.Khoj.appointment.AppointmentUpdateRequestDTO;
import com.rohan.Khoj.appointment.AppointmentRepository;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationRepository;
import com.rohan.Khoj.clinic.ClinicRepository;
import com.rohan.Khoj.doctor.DoctorRepository;
import com.rohan.Khoj.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.affiliation.AffiliationStatus;
import com.rohan.Khoj.patient.PatientService;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.clinic.ClinicService;
import com.rohan.Khoj.doctor.DoctorService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final DoctorClinicAffiliationRepository affiliationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    @Transactional
    public AppointmentDTO scheduleAppointment(AppointmentRequestDTO requestDTO) {
        // 1. Check Affiliation: Verify the doctor is affiliated with the clinic.
        // Wait, requestDTO still uses doctorId and clinicId. I will look it up.
        // Since we changed to UUID for affiliation, this logic needs to be refactored based on how we fetch affiliation.
        // Let's assume there's a findByDoctorIdAndClinicId or we just use doctor and clinic objects.
        
        DoctorEntity doctorEntity = doctorRepository.findById(requestDTO.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
        ClinicEntity clinicEntity = clinicRepository.findById(requestDTO.getClinicId())
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found."));
                
        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findByDoctorAndClinic(doctorEntity, clinicEntity)
                .orElseThrow(() -> new IllegalArgumentException("Doctor is not affiliated with this clinic."));

        if (affiliation.getStatus() != AffiliationStatus.APPROVED) {
            throw new IllegalStateException("Affiliation between doctor and clinic is not active.");
        }

        // 2. Check Valid Slot: Extract the slot and verify it's a valid working slot.
        LocalDateTime appointmentDateTime = LocalDateTime.of(requestDTO.getAppointmentDate(), requestDTO.getAppointmentTime());
        String slotKey = getSlotKey(appointmentDateTime);

        if (!isValidShift(affiliation, appointmentDateTime)) {
            throw new IllegalStateException("Appointment time is outside the doctor's working hours.");
        }

        // 3. Check Patient Limit: Ensure the slot is not full.
        long appointmentsBooked = appointmentRepository.countByAffiliationAndAppointmentTimeSlot(
                affiliation, slotKey);

        if (appointmentsBooked >= affiliation.getDailyPatientLimit()) {
            throw new IllegalStateException("Appointment slot is full. Please choose another time.");
        }

        // 4. Check for patient's double-booking
        if (hasPatientBooked(requestDTO.getPatientId(), affiliation.getId(), slotKey)) {
            throw new IllegalStateException("You already have an appointment in this slot.");
        }

        // 5. Create and save the new appointment
        // Corrected section: Fetch the entities before building the appointment
        PatientEntity patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        DoctorEntity doctor = affiliation.getDoctor();
        ClinicEntity clinic = affiliation.getClinic();

        AppointmentDetailEntity newAppointment = AppointmentDetailEntity.builder()
                .patient(patient)
                .affiliation(affiliation)
                .appointmentDate(requestDTO.getAppointmentDate())
                .appointmentTime(requestDTO.getAppointmentTime())
                .appointmentTimeSlot(slotKey)
                .reason(requestDTO.getReason())
                .status("SCHEDULED")
                .build();

        AppointmentDetailEntity savedAppointment = appointmentRepository.save(newAppointment);

        // 6. Map the saved entity to a DTO for the response
        // The re-fetching is no longer needed since the entities are correctly attached.
        UUID id = savedAppointment.getId();
        LocalDate appointmentDate = savedAppointment.getAppointmentDate();
        LocalTime appointmentTime = savedAppointment.getAppointmentTime();
        String reason = savedAppointment.getReason();
        String status = savedAppointment.getStatus();
        UUID patientId = savedAppointment.getPatient().getId();
        String patientFullName = savedAppointment.getPatient().getFirstName() + " " + savedAppointment.getPatient().getLastName();
        UUID doctorId = affiliation.getDoctor().getId();
        String doctorFullName = affiliation.getDoctor().getFirstName() + " " + affiliation.getDoctor().getLastName();
        
        Set<String> doctorSpecialization = new HashSet<>(Arrays.asList(affiliation.getDoctor().getSpecializations().split(",")));
        
        UUID clinicId = affiliation.getClinic().getId();
        String clinicName = affiliation.getClinic().getName();

        return new AppointmentDTO(id, appointmentDate, appointmentTime, reason, status, patientId, patientFullName, doctorId, doctorFullName, doctorSpecialization, clinicId, clinicName);
    }

    private boolean isValidShift(DoctorClinicAffiliationEntity affiliation, LocalDateTime appointmentTime) {
        String dayOfWeek = appointmentTime.getDayOfWeek().name().toUpperCase();
        LocalTime startTime = null;
        LocalTime endTime = null;
        
        switch (dayOfWeek) {
            case "MONDAY": startTime = affiliation.getMondayStart(); endTime = affiliation.getMondayEnd(); break;
            case "TUESDAY": startTime = affiliation.getTuesdayStart(); endTime = affiliation.getTuesdayEnd(); break;
            case "WEDNESDAY": startTime = affiliation.getWednesdayStart(); endTime = affiliation.getWednesdayEnd(); break;
            case "THURSDAY": startTime = affiliation.getThursdayStart(); endTime = affiliation.getThursdayEnd(); break;
            case "FRIDAY": startTime = affiliation.getFridayStart(); endTime = affiliation.getFridayEnd(); break;
            case "SATURDAY": startTime = affiliation.getSaturdayStart(); endTime = affiliation.getSaturdayEnd(); break;
            case "SUNDAY": startTime = affiliation.getSundayStart(); endTime = affiliation.getSundayEnd(); break;
        }

        if (startTime == null || endTime == null) {
            return false;
        }

        LocalTime appointmentLocalTime = appointmentTime.toLocalTime();
        return !(appointmentLocalTime.isBefore(startTime) || appointmentLocalTime.isAfter(endTime));
    }

    private boolean hasPatientBooked(UUID patientId, UUID affiliationId, String slotKey) {
        long existingBookings = appointmentRepository.countByPatientIdAndAffiliationIdAndAppointmentTimeSlot(patientId, affiliationId, slotKey);
        return existingBookings > 0;
    }

    private String getSlotKey(LocalDateTime appointmentTime) {
        // Format the day of the week (e.g., "Monday")
        String day = appointmentTime.getDayOfWeek().name();

        // Format the hour (e.g., "09")
        int hour = appointmentTime.getHour();
        String formattedHour = String.format("%02d", hour);

        // Assuming one-hour slots, the minute part is always "00"
        String formattedTime = formattedHour + ":00";

        return String.format("%s_%s", day, formattedTime); // e.g., "MONDAY_09:00"
    }

    private boolean isSlotAvailable(DoctorClinicAffiliationEntity affiliation, String slotKey) {
        // Check if the slot key exists in the dailyPatientLimit map
        Integer patientLimit = affiliation.getDailyPatientLimit();
        if (patientLimit == null) {
            return false; // Slot is not a valid working slot for the doctor
        }

        // Get the number of appointments already booked for this slot
        long appointmentsBooked = appointmentRepository.countByAffiliationAndAppointmentTimeSlot(
                affiliation, slotKey);

        return appointmentsBooked < patientLimit;
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    // ... (getAppointmentById, getAppointmentsForPatient, etc. remain the same as they were already correct) ...
    // ... (controller code also remains correct and doesn't need changes) ...

    @Transactional
    public AppointmentDTO updateAppointment(UUID id, AppointmentUpdateRequestDTO updateRequestDTO) {
        // The @EntityGraph on findById ensures all relations are loaded for the update.
        AppointmentDetailEntity appointmentToUpdate = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        modelMapper.map(updateRequestDTO, appointmentToUpdate);

        AppointmentDetailEntity updatedAppointment = appointmentRepository.save(appointmentToUpdate);

        // No re-fetch needed here either.
        return modelMapper.map(updatedAppointment, AppointmentDTO.class);
    }

    @Transactional
    public AppointmentDTO deleteAppointment(UUID id) {
        // The @EntityGraph on findById ensures the object is fully loaded before deletion
        // so it can be returned correctly.
        AppointmentDetailEntity appointmentToDelete = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointmentRepository.delete(appointmentToDelete);

        return modelMapper.map(appointmentToDelete, AppointmentDTO.class);
    }

    // Other service methods from your original code are correct and can be included here
    public Optional<AppointmentDTO> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class));
    }

    public List<AppointmentDTO> getAppointmentsForPatient(UUID patientId) {
        PatientEntity patient = patientService.getPatientEntityById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        return appointmentRepository.findByPatient(patient).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForDoctor(UUID doctorId) {
        // Since we changed to affiliations, we should probably fetch affiliations for doctor and then fetch appointments for each affiliation
        return new ArrayList<>(); // To be implemented properly later
    }

    public List<AppointmentDTO> getAppointmentsForClinic(UUID clinicId) {
        // Similar to doctor
        return new ArrayList<>(); // To be implemented properly later
    }

    public List<AppointmentDTO> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForDoctorOnDate(UUID doctorId, LocalDate date) {
        return new ArrayList<>(); // To be implemented properly later
    }
}