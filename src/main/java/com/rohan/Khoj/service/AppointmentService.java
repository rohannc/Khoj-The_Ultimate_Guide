package com.rohan.Khoj.service;

// Removed unused @Autowired as @RequiredArgsConstructor is used
// Removed unused imports if any from previous versions (e.g., ClinicModelMapperConfig)
import com.rohan.Khoj.customException.BadRequestException;
import com.rohan.Khoj.customException.ConflictException; // If you need specific appointment conflicts
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.AppointmentDTO; // New response DTO
import com.rohan.Khoj.dto.AppointmentRequestDTO; // New request DTO
import com.rohan.Khoj.dto.AppointmentUpdateRequestDTO; // New update DTO
import com.rohan.Khoj.entity.AppointmentDetailEntity;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok annotation for constructor injection
@Transactional(readOnly = true) // Default for service methods
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper; // Inject ModelMapper
    private final PatientService patientService; // To fetch Patient object
    private final DoctorService doctorService;   // To fetch Doctor object
    private final ClinicService clinicService;   // To fetch Clinic object

    // --- Create Operation ---

    /**
     * Schedules a new appointment based on the provided DTO.
     * Validates existence of associated entities and sets default status.
     *
     * @param requestDTO The DTO containing appointment details.
     * @return The created AppointmentDTO.
     * @throws ResourceNotFoundException if patient, doctor, or clinic not found.
     * @throws BadRequestException for business rule violations (e.g., availability).
     */
    @Transactional
    public AppointmentDTO scheduleAppointment(AppointmentRequestDTO requestDTO) {
        // 1. Fetch associated entities by ID from their respective services (using new get*EntityById methods)
        PatientEntity patient = patientService.getPatientEntityById(requestDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + requestDTO.getPatientId()));
        DoctorEntity doctor = doctorService.getDoctorEntityById(requestDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + requestDTO.getDoctorId()));
        ClinicEntity clinic = clinicService.getClinicEntityById(requestDTO.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + requestDTO.getClinicId()));

        // 2. Map DTO to Entity for basic fields
        AppointmentDetailEntity appointment = modelMapper.map(requestDTO, AppointmentDetailEntity.class);

        // 3. Set the fetched managed entities back to the appointment object
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);

        // 4. Set default status and timestamps (if present in entity)
        appointment.setStatus("Scheduled"); // Default status for new appointments
        // appointment.setCreatedAt(LocalDateTime.now()); // If you have createdAt/updatedAt in entity

        // 5. Further business validation (e.g., availability checks)
        // This part can be complex and depends on specific business rules.
        // Example:
        /*
        if (!doctorService.isDoctorAvailable(doctor.getId(), requestDTO.getAppointmentDate(), requestDTO.getAppointmentTime())) {
             throw new BadRequestException("Doctor is not available at this time.");
        }
        if (!clinicService.isClinicOpen(clinic.getId(), requestDTO.getAppointmentDate(), requestDTO.getAppointmentTime())) {
             throw new BadRequestException("Clinic is closed at this time.");
        }
        // Check for double booking
        if (appointmentRepository.findByDoctorAndAppointmentDateAndAppointmentTime(doctor, requestDTO.getAppointmentDate(), requestDTO.getAppointmentTime()).isPresent()) {
            throw new ConflictException("Doctor already has an appointment at this specific time.");
        }
        */

        // 6. Save the entity
        AppointmentDetailEntity savedAppointment = appointmentRepository.save(appointment);

        // 7. Map and return the response DTO
        return modelMapper.map(savedAppointment, AppointmentDTO.class);
    }

    // --- Retrieval Operations ---

    /**
     * Retrieves all appointments, mapped to DTOs.
     * @return A list of AppointmentDTO.
     */
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds an appointment by its ID, mapped to a DTO.
     * @param id The UUID of the appointment to search for.
     * @return An Optional containing the AppointmentDTO if found.
     */
    public Optional<AppointmentDTO> getAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class));
    }

    /**
     * Finds appointments for a specific patient, mapped to DTOs.
     * @param patientId The UUID of the patient.
     * @return A list of AppointmentDTO for the given patient.
     * @throws ResourceNotFoundException if the patient is not found.
     */
    public List<AppointmentDTO> getAppointmentsForPatient(UUID patientId) {
        PatientEntity patient = patientService.getPatientEntityById(patientId) // Use get*EntityById helper
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
        return appointmentRepository.findByPatient(patient).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds appointments for a specific doctor, mapped to DTOs.
     * @param doctorId The UUID of the doctor.
     * @return A list of AppointmentDTO for the given doctor.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    public List<AppointmentDTO> getAppointmentsForDoctor(UUID doctorId) {
        DoctorEntity doctor = doctorService.getDoctorEntityById(doctorId) // Use get*EntityById helper
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        return appointmentRepository.findByDoctor(doctor).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds appointments for a specific clinic, mapped to DTOs.
     * @param clinicId The UUID of the clinic.
     * @return A list of AppointmentDTO for the given clinic.
     * @throws ResourceNotFoundException if the clinic is not found.
     */
    public List<AppointmentDTO> getAppointmentsForClinic(UUID clinicId) {
        ClinicEntity clinic = clinicService.getClinicEntityById(clinicId) // Use get*EntityById helper
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with ID: " + clinicId));
        return appointmentRepository.findByClinic(clinic).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds appointments by status, mapped to DTOs.
     * @param status The status to search for (e.g., "Scheduled", "Cancelled").
     * @return A list of AppointmentDTO matching the status.
     */
    public List<AppointmentDTO> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds appointments for a specific doctor on a specific date, mapped to DTOs.
     * @param doctorId The UUID of the doctor.
     * @param date The date of the appointment.
     * @return A list of AppointmentDTO for the given doctor and date.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    public List<AppointmentDTO> getAppointmentsForDoctorOnDate(UUID doctorId, LocalDate date) {
        DoctorEntity doctor = doctorService.getDoctorEntityById(doctorId) // Use get*EntityById helper
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        return appointmentRepository.findByDoctorAndAppointmentDate(doctor, date).stream()
                .map(appointmentEntity -> modelMapper.map(appointmentEntity, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    // --- Update Operation ---

    /**
     * Updates an existing appointment's details based on the provided DTO.
     *
     * @param id The UUID of the appointment to update.
     * @param updateRequestDTO The DTO containing the updated appointment details.
     * @return The updated AppointmentDTO.
     * @throws ResourceNotFoundException if the appointment is not found.
     * @throws BadRequestException for business rule violations during update.
     */
    @Transactional
    public AppointmentDTO updateAppointment(UUID id, AppointmentUpdateRequestDTO updateRequestDTO) {
        AppointmentDetailEntity appointmentToUpdate = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        // Note: Patient, Doctor, Clinic are typically NOT updated directly through this DTO.
        // Changing them usually implies canceling and rescheduling a new appointment.
        // If allowed (e.g., if DTO contains Optional<UUID> for these IDs), you'd need logic here to:
        // 1. Check if the ID in DTO is present and different from current.
        // 2. Fetch the new entity using get*EntityById.
        // 3. Set the new entity on appointmentToUpdate.

        // Map DTO to entity for basic fields (date, time, reason, status)
        modelMapper.map(updateRequestDTO, appointmentToUpdate);

        // Update updatedAt timestamp (if present in entity)
        // appointmentToUpdate.setUpdatedAt(LocalDateTime.now());

        // Further validation for updates (e.g., status transitions, availability changes)
        /*
        // Example: If status changed to "Cancelled", add cancellation specific logic.
        if ("Cancelled".equals(updateRequestDTO.getStatus()) && !"Cancelled".equals(appointmentToUpdate.getStatus())) {
            // Perform cancellation specific logic, e.g., notify doctor/patient
        }
        // Example: Re-check availability if date/time changed
        if (updateRequestDTO.getAppointmentDate() != null || updateRequestDTO.getAppointmentTime() != null) {
            // Re-check doctor/clinic availability for the potentially new date/time
            // if (!doctorService.isDoctorAvailable(appointmentToUpdate.getDoctor().getId(), appointmentToUpdate.getAppointmentDate(), appointmentToUpdate.getAppointmentTime())) {
            //      throw new BadRequestException("Doctor is no longer available at this updated time.");
            // }
        }
        */

        AppointmentDetailEntity updatedAppointmentEntity = appointmentRepository.save(appointmentToUpdate);
        return modelMapper.map(updatedAppointmentEntity, AppointmentDTO.class);
    }

    // --- Delete Operation ---

    /**
     * Deletes an appointment by its ID.
     *
     * @param id The UUID of the appointment to delete.
     * @return The AppointmentDTO of the appointment that was deleted.
     * @throws ResourceNotFoundException if the appointment with the given ID is not found.
     */
    @Transactional
    public AppointmentDTO deleteAppointment(UUID id) {
        AppointmentDetailEntity appointmentToDelete = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appointmentRepository.delete(appointmentToDelete);

        return modelMapper.map(appointmentToDelete, AppointmentDTO.class);
    }
}