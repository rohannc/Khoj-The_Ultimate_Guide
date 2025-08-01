package com.rohan.Khoj.service;

import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.AppointmentDTO;
import com.rohan.Khoj.dto.AppointmentRequestDTO;
import com.rohan.Khoj.dto.AppointmentUpdateRequestDTO;
import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import com.rohan.Khoj.entity.*;
import com.rohan.Khoj.repository.AppointmentRepository;
import com.rohan.Khoj.repository.DoctorClinicAffiliationRepository;
import com.rohan.Khoj.repository.PatientRepository;
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
    private final PatientRepository patientRepository; // Add PatientRepository here

    @Transactional
    public AppointmentDTO scheduleAppointment(AppointmentRequestDTO requestDTO) {
        // 1. Check Affiliation: Verify the doctor is affiliated with the clinic.
        DoctorClinicAffiliationId affiliationId = new DoctorClinicAffiliationId(requestDTO.getDoctorId(), requestDTO.getClinicId());
        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findById(affiliationId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor is not affiliated with this clinic."));

        if (affiliation.getStatus() != AffiliationStatus.APPROVED) {
            throw new IllegalStateException("Affiliation between doctor and clinic is not active.");
        }

        // 2. Check Valid Slot: Extract the slot and verify it's a valid working slot.
        LocalDateTime appointmentDateTime = LocalDateTime.of(requestDTO.getAppointmentDate(), requestDTO.getAppointmentTime());
        String slotKey = getSlotKey(appointmentDateTime);

        if (affiliation.getShiftDetails() == null || !isValidShift(affiliation.getShiftDetails(), appointmentDateTime)) {
            throw new IllegalStateException("Appointment time is outside the doctor's working hours.");
        }

        // 3. Check Patient Limit: Ensure the slot is not full.
        long appointmentsBooked = appointmentRepository.countByDoctorAndClinicAndAppointmentTimeSlot(
                affiliation.getDoctor(), affiliation.getClinic(), slotKey);

        if (appointmentsBooked >= affiliation.getPatientLimits()) {
            throw new IllegalStateException("Appointment slot is full. Please choose another time.");
        }

        // 4. Check for patient's double-booking
        if (hasPatientBooked(requestDTO.getPatientId(), requestDTO.getDoctorId(), requestDTO.getClinicId(), slotKey)) {
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
                .doctor(doctor)
                .clinic(clinic)
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
        UUID doctorId = savedAppointment.getDoctor().getId();
        String doctorFullName = savedAppointment.getDoctor().getFirstName() + " " + savedAppointment.getDoctor().getLastName();
        Set<String> doctorSpecialization = savedAppointment.getDoctor().getSpecialization();
        UUID clinicId = savedAppointment.getClinic().getId();
        String clinicName = savedAppointment.getClinic().getName();

        return new AppointmentDTO(id, appointmentDate, appointmentTime, reason, status, patientId, patientFullName, doctorId, doctorFullName, doctorSpecialization, clinicId, clinicName);
    }

    private boolean isValidShift(Map<String, String> shiftDetails, LocalDateTime appointmentTime) {
        String dayOfWeek = appointmentTime.getDayOfWeek().name();
        String shift = shiftDetails.get(dayOfWeek.toLowerCase());
        if (shift == null) {
            System.out.println(dayOfWeek);
            System.out.println("Wrong Day");
            return false; // Doctor is not working on this day
        }

        // Parse the shift string (e.g., "09:00-17:00") and check if the appointment time is within it
        String[] times = shift.split("-");
        LocalTime startTime = LocalTime.parse(times[0]);
        LocalTime endTime = LocalTime.parse(times[1]);
        LocalTime appointmentLocalTime = appointmentTime.toLocalTime();

        return !(appointmentLocalTime.isBefore(startTime) || appointmentLocalTime.isAfter(endTime));
    }

    private boolean hasPatientBooked(UUID patientId, UUID doctorId, UUID clinicId, String slotKey) {
        long existingBookings = appointmentRepository.countByPatientIdAndDoctorIdAndClinicIdAndAppointmentTimeSlot(patientId, doctorId, clinicId, slotKey);
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
        // Check if the slot key exists in the patientLimits map
        Integer patientLimit = affiliation.getPatientLimits();
        if (patientLimit == null) {
            return false; // Slot is not a valid working slot for the doctor
        }

        // Get the number of appointments already booked for this slot
        long appointmentsBooked = appointmentRepository.countByDoctorAndClinicAndAppointmentTimeSlot(
                affiliation.getDoctor(), affiliation.getClinic(), slotKey);

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
        DoctorEntity doctor = doctorService.getDoctorEntityById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        return appointmentRepository.findByDoctor(doctor).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForClinic(UUID clinicId) {
        ClinicEntity clinic = clinicService.getClinicEntityById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + clinicId));
        return appointmentRepository.findByClinic(clinic).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForDoctorOnDate(UUID doctorId, LocalDate date) {
        DoctorEntity doctor = doctorService.getDoctorEntityById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        return appointmentRepository.findByDoctorAndAppointmentDate(doctor, date).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }
}