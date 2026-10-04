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

        // 2. Check Doctor's Working Day: Verify the doctor has working hours on the selected day
        if (!isWorkingDay(affiliation, requestDTO.getAppointmentDate().getDayOfWeek().name().toUpperCase())) {
            throw new IllegalStateException("Doctor does not practice at this clinic on " + requestDTO.getAppointmentDate().getDayOfWeek());
        }

        // 3. Check Daily Patient Limit: Ensure the daily limit for this affiliation on this date is not full.
        long appointmentsBooked = appointmentRepository.countByAffiliationAndAppointmentDate(
                affiliation, requestDTO.getAppointmentDate());

        if (appointmentsBooked >= affiliation.getDailyPatientLimit()) {
            throw new IllegalStateException("Doctor's appointment limit for this day is full. Please choose another date.");
        }

        // 4. Check for patient's double-booking on the same day with this affiliation
        if (appointmentRepository.countByPatientIdAndAffiliationIdAndAppointmentDate(
                requestDTO.getPatientId(), affiliation.getId(), requestDTO.getAppointmentDate()) > 0) {
            throw new IllegalStateException("You already have an appointment with this doctor/clinic on this date.");
        }

        // 5. Create and save the new appointment
        PatientEntity patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));

        AppointmentDetailEntity newAppointment = AppointmentDetailEntity.builder()
                .patient(patient)
                .affiliation(affiliation)
                .appointmentDate(requestDTO.getAppointmentDate())
                .appointmentTime(requestDTO.getAppointmentTime()) // null or assigned
                .tokenNumber(null) // Assigned later by clinic
                .reason(requestDTO.getReason())
                .status("SCHEDULED")
                .build();

        AppointmentDetailEntity savedAppointment = appointmentRepository.save(newAppointment);

        // 6. Map the saved entity to DTO using ModelMapper
        return modelMapper.map(savedAppointment, AppointmentDTO.class);
    }

    private boolean isWorkingDay(DoctorClinicAffiliationEntity affiliation, String dayOfWeek) {
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

        return startTime != null && endTime != null;
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
        AppointmentDetailEntity appointmentToUpdate = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (updateRequestDTO.getAppointmentDate() != null) {
            appointmentToUpdate.setAppointmentDate(updateRequestDTO.getAppointmentDate());
        }
        if (updateRequestDTO.getAppointmentTime() != null) {
            appointmentToUpdate.setAppointmentTime(updateRequestDTO.getAppointmentTime());
        }
        if (updateRequestDTO.getTokenNumber() != null) {
            appointmentToUpdate.setTokenNumber(updateRequestDTO.getTokenNumber());
        }
        if (updateRequestDTO.getReason() != null) {
            appointmentToUpdate.setReason(updateRequestDTO.getReason());
        }
        if (updateRequestDTO.getStatus() != null) {
            appointmentToUpdate.setStatus(updateRequestDTO.getStatus());
        }

        AppointmentDetailEntity updatedAppointment = appointmentRepository.save(appointmentToUpdate);
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
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForClinic(UUID clinicId) {
        return appointmentRepository.findByClinicId(clinicId).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForDoctorOnDate(UUID doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsForClinicOnDate(UUID clinicId, LocalDate date) {
        return appointmentRepository.findByClinicIdAndDate(clinicId, date).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }
}