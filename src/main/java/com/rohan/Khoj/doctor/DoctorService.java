package com.rohan.Khoj.doctor;

import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.exception.ConflictException;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.doctor.DoctorDTO;
import com.rohan.Khoj.doctor.DoctorUpdateRequestDTO;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rohan.Khoj.common.PasswordUpdateRequestDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default for all read operations
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.rohan.Khoj.appointment.AppointmentRepository appointmentRepository;
    private final com.rohan.Khoj.appointment.AppointmentService appointmentService;
    private final com.rohan.Khoj.affiliation.AffiliationService affiliationService;
    private final com.rohan.Khoj.patient.PatientRepository patientRepository;
    private final com.rohan.Khoj.prescription.PrescriptionRepository prescriptionRepository;
    private final com.rohan.Khoj.prescription.PrescriptionService prescriptionService;

    // --- Update Operations ---

    /**
     * Updates an existing doctor's profile details.
     * Password updates are handled by a separate, dedicated method.
     *
     * @param id The UUID of the doctor to update.
     * @param updateRequestDTO The DTO with new profile data.
     * @return The updated DoctorDTO.
     * @throws ResourceNotFoundException if the doctor is not found.
     * @throws ConflictException if the new username or email is already taken.
     */
    @Transactional
    public DoctorDTO updateDoctor(UUID id, DoctorUpdateRequestDTO updateRequestDTO) {
        DoctorEntity doctorToUpdate = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        // --- Handle Username Update ---
        if (updateRequestDTO.getUsername() != null && !updateRequestDTO.getUsername().equals(doctorToUpdate.getUsername())) {
            if (doctorRepository.findByUsername(updateRequestDTO.getUsername()).isPresent()) {
                throw new ConflictException("Username '" + updateRequestDTO.getUsername() + "' is already taken.");
            }
            doctorToUpdate.setUsername(updateRequestDTO.getUsername());
        }

        // --- Handle Email Update ---
        if (updateRequestDTO.getEmailId() != null && !updateRequestDTO.getEmailId().equals(doctorToUpdate.getEmailId())) {
            if (doctorRepository.findByEmailId(updateRequestDTO.getEmailId()).isPresent()) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmailId() + "' is already in use.");
            }
            doctorToUpdate.setEmailId(updateRequestDTO.getEmailId());
        }

        // --- Map other non-sensitive fields ---
        modelMapper.map(updateRequestDTO, doctorToUpdate);

        doctorToUpdate.setUpdatedAt(LocalDateTime.now());
        DoctorEntity updatedDoctor = doctorRepository.save(doctorToUpdate);

        return modelMapper.map(updatedDoctor, DoctorDTO.class);
    }

    /**
     * Updates the password for a specific doctor after verifying the current one.
     *
     * @param id The UUID of the doctor.
     * @param passwordRequest The DTO containing the current and new passwords.
     * @throws ResourceNotFoundException if the doctor is not found.
     * @throws BadRequestException if the current password is incorrect.
     */
    @Transactional
    public void updatePassword(UUID id, PasswordUpdateRequestDTO passwordRequest) {
        DoctorEntity doctorToUpdate = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), doctorToUpdate.getPassword())) {
            throw new BadRequestException("Incorrect current password.");
        }

        doctorToUpdate.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        doctorToUpdate.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctorToUpdate);
    }

    // --- Retrieval Operations ---

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<DoctorDTO> getDoctorById(UUID id) {
        return doctorRepository.findById(id)
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class));
    }

    public Optional<DoctorEntity> getDoctorEntityById(UUID id) {
        return doctorRepository.findById(id);
    }

    public Optional<DoctorDTO> getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username)
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class));
    }

    public Optional<DoctorDTO> getDoctorByEmail(String email) {
        return doctorRepository.findByEmailId(email)
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class));
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationsContainingIgnoreCase(specialization).stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    public List<DoctorDTO> getDoctorsByQualification(String qualification) {
        return doctorRepository.findByQualificationsContainingIgnoreCase(qualification).stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    public List<DoctorDTO> getDoctorsByLastName(String lastName) {
        return doctorRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    // --- Delete Operation ---

    /**
     * Deletes a doctor by their ID.
     *
     * @param id The UUID of the doctor to delete.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    @Transactional
    public void deleteDoctor(UUID id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    public org.springframework.data.domain.Page<DoctorDTO> searchDoctors(String query, String specialization, String city, com.rohan.Khoj.common.Gender gender, org.springframework.data.domain.Pageable pageable) {
        return doctorRepository.searchDoctors(query, specialization, city, gender, pageable)
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class));
    }

    public List<com.rohan.Khoj.patient.PatientDTO> getDoctorPatients(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        return patientRepository.findDistinctPatientsByDoctorId(doctorId).stream()
                .map(patient -> modelMapper.map(patient, com.rohan.Khoj.patient.PatientDTO.class))
                .collect(Collectors.toList());
    }

    public DoctorDashboardDTO getDoctorDashboard(UUID doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        DoctorDTO profile = modelMapper.map(doctor, DoctorDTO.class);

        java.time.LocalDate today = java.time.LocalDate.now();

        // Appointments
        List<com.rohan.Khoj.appointment.AppointmentDTO> todayAppointments = appointmentService.getAppointmentsForDoctorOnDate(doctorId, today);
        List<com.rohan.Khoj.appointment.AppointmentDTO> allDoctorAppointments = appointmentService.getAppointmentsForDoctor(doctorId);
        List<com.rohan.Khoj.appointment.AppointmentDTO> upcomingAppointments = allDoctorAppointments.stream()
                .filter(a -> a.getAppointmentDate() != null && !a.getAppointmentDate().isBefore(today))
                .limit(10)
                .collect(Collectors.toList());

        // Affiliations
        List<com.rohan.Khoj.affiliation.AffiliationResponseDTO> activeAffiliations = 
                affiliationService.getAffiliationsForDoctor(doctorId, com.rohan.Khoj.affiliation.AffiliationStatus.APPROVED);
        List<com.rohan.Khoj.affiliation.AffiliationResponseDTO> pendingAffiliations = 
                affiliationService.getAffiliationsForDoctor(doctorId, com.rohan.Khoj.affiliation.AffiliationStatus.PENDING);

        // Patients
        List<com.rohan.Khoj.patient.PatientDTO> patients = getDoctorPatients(doctorId);

        // Prescriptions
        List<com.rohan.Khoj.prescription.PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
        List<com.rohan.Khoj.prescription.PrescriptionDTO> recentPrescriptions = prescriptions.stream().limit(10).collect(Collectors.toList());

        // Metrics
        long totalAppointments = appointmentRepository.countByDoctorId(doctorId);
        long todayAppointmentsCount = todayAppointments.size();
        long totalPatients = patients.size();
        long totalRx = prescriptionRepository.countByDoctorId(doctorId);

        return DoctorDashboardDTO.builder()
                .profile(profile)
                .totalPatients(totalPatients)
                .todayAppointmentsCount(todayAppointmentsCount)
                .totalAppointments(totalAppointments)
                .activeAffiliationsCount(activeAffiliations.size())
                .pendingAffiliationsCount(pendingAffiliations.size())
                .totalPrescriptionsIssued(totalRx)
                .todayAppointments(todayAppointments)
                .upcomingAppointments(upcomingAppointments)
                .activeAffiliations(activeAffiliations)
                .pendingAffiliations(pendingAffiliations)
                .recentPatients(patients.stream().limit(10).collect(Collectors.toList()))
                .recentPrescriptions(recentPrescriptions)
                .build();
    }
}
