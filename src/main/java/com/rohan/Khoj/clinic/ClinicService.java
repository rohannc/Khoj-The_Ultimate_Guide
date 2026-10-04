package com.rohan.Khoj.clinic;

import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.exception.ConflictException;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.clinic.ClinicDTO;
import com.rohan.Khoj.clinic.ClinicUpdateRequestDTO;
import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.clinic.ClinicRepository;
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
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.rohan.Khoj.appointment.AppointmentRepository appointmentRepository;
    private final com.rohan.Khoj.appointment.AppointmentService appointmentService;
    private final com.rohan.Khoj.affiliation.AffiliationService affiliationService;
    private final com.rohan.Khoj.patient.PatientRepository patientRepository;
    private final com.rohan.Khoj.doctor.DoctorRepository doctorRepository;

    // --- Update Operations ---

    /**
     * Updates an existing clinic's profile details.
     * Password changes are handled by the dedicated updatePassword method.
     *
     * @param id The UUID of the clinic to update.
     * @param updateRequestDTO The DTO with new profile data.
     * @return The updated ClinicDTO.
     * @throws ResourceNotFoundException if the clinic is not found.
     * @throws ConflictException if the new clinic name or email is already in use.
     */
    @Transactional
    public ClinicDTO updateClinic(UUID id, ClinicUpdateRequestDTO updateRequestDTO) {
        ClinicEntity clinicToUpdate = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        // --- Handle Clinic Name Update ---
        if (updateRequestDTO.getName() != null && !updateRequestDTO.getName().equals(clinicToUpdate.getName())) {
            if (clinicRepository.findByName(updateRequestDTO.getName()).isPresent()) {
                throw new ConflictException("A clinic with the name '" + updateRequestDTO.getName() + "' already exists.");
            }
            clinicToUpdate.setName(updateRequestDTO.getName());
        }

        // --- Handle Email Update ---
        if (updateRequestDTO.getEmailId() != null && !updateRequestDTO.getEmailId().equals(clinicToUpdate.getEmailId())) {
            if (clinicRepository.findByEmailId(updateRequestDTO.getEmailId()).isPresent()) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmailId() + "' is already in use.");
            }
            clinicToUpdate.setEmailId(updateRequestDTO.getEmailId());
        }

        // --- Map other non-sensitive fields ---
        modelMapper.map(updateRequestDTO, clinicToUpdate);

        clinicToUpdate.setUpdatedAt(LocalDateTime.now());
        ClinicEntity updatedClinic = clinicRepository.save(clinicToUpdate);

        return modelMapper.map(updatedClinic, ClinicDTO.class);
    }

    /**
     * Updates the password for a specific clinic after verifying the current password.
     *
     * @param id The UUID of the clinic.
     * @param passwordRequest The DTO containing the current and new passwords.
     * @throws ResourceNotFoundException if the clinic is not found.
     * @throws BadRequestException if the current password is incorrect.
     */
    @Transactional
    public void updatePassword(UUID id, PasswordUpdateRequestDTO passwordRequest) {
        ClinicEntity clinicToUpdate = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), clinicToUpdate.getPassword())) {
            throw new BadRequestException("Incorrect current password.");
        }

        clinicToUpdate.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        clinicToUpdate.setUpdatedAt(LocalDateTime.now());
        clinicRepository.save(clinicToUpdate);
    }

    // --- Retrieval Operations ---

    public List<ClinicDTO> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<ClinicDTO> getClinicById(UUID id) {
        return clinicRepository.findById(id)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public Optional<ClinicEntity> getClinicEntityById(UUID id) {
        return clinicRepository.findById(id);
    }

    public Optional<ClinicDTO> getClinicByName(String name) {
        return clinicRepository.findByName(name)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public Optional<ClinicDTO> getClinicByEmail(String email) {
        return clinicRepository.findByEmailId(email)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public List<ClinicDTO> getClinicsByCity(String city) {
        return clinicRepository.findByCityContainingIgnoreCase(city).stream()
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    public List<ClinicDTO> getClinicsByPinCode(String pinCode) {
        return clinicRepository.findByPinCode(pinCode).stream()
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    // --- Delete Operation ---

    /**
     * Deletes a clinic by its ID.
     *
     * @param id The UUID of the clinic to delete.
     * @throws ResourceNotFoundException if the clinic is not found.
     */
    @Transactional
    public void deleteClinic(UUID id) {
        if (!clinicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clinic not found with id: " + id);
        }
        clinicRepository.deleteById(id);
    }

    public org.springframework.data.domain.Page<ClinicDTO> searchClinics(String query, String city, String state, String pinCode, org.springframework.data.domain.Pageable pageable) {
        return clinicRepository.searchClinics(query, city, state, pinCode, pageable)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public List<com.rohan.Khoj.patient.PatientDTO> getClinicPatients(UUID clinicId) {
        if (!clinicRepository.existsById(clinicId)) {
            throw new ResourceNotFoundException("Clinic not found with id: " + clinicId);
        }
        return patientRepository.findDistinctPatientsByClinicId(clinicId).stream()
                .map(patient -> modelMapper.map(patient, com.rohan.Khoj.patient.PatientDTO.class))
                .collect(Collectors.toList());
    }

    public ClinicDashboardDTO getClinicDashboard(UUID clinicId) {
        ClinicEntity clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + clinicId));
        ClinicDTO profile = modelMapper.map(clinic, ClinicDTO.class);

        java.time.LocalDate today = java.time.LocalDate.now();

        // Appointments
        List<com.rohan.Khoj.appointment.AppointmentDTO> todayAppointments = appointmentService.getAppointmentsForClinicOnDate(clinicId, today);
        List<com.rohan.Khoj.appointment.AppointmentDTO> allClinicAppointments = appointmentService.getAppointmentsForClinic(clinicId);
        List<com.rohan.Khoj.appointment.AppointmentDTO> upcomingAppointments = allClinicAppointments.stream()
                .filter(a -> a.getAppointmentDate() != null && !a.getAppointmentDate().isBefore(today))
                .limit(10)
                .collect(Collectors.toList());

        // Affiliations
        List<com.rohan.Khoj.affiliation.AffiliationResponseDTO> activeAffiliations = 
                affiliationService.getAffiliationsForClinic(clinicId, com.rohan.Khoj.affiliation.AffiliationStatus.APPROVED);
        List<com.rohan.Khoj.affiliation.AffiliationResponseDTO> pendingAffiliations = 
                affiliationService.getAffiliationsForClinic(clinicId, com.rohan.Khoj.affiliation.AffiliationStatus.PENDING);

        // Affiliated Doctors
        List<com.rohan.Khoj.doctor.DoctorDTO> affiliatedDoctors = activeAffiliations.stream()
                .map(aff -> aff.getDoctorId())
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(doctorRepository::findById)
                .filter(Optional::isPresent)
                .map(opt -> modelMapper.map(opt.get(), com.rohan.Khoj.doctor.DoctorDTO.class))
                .collect(Collectors.toList());

        // Patients
        List<com.rohan.Khoj.patient.PatientDTO> patients = getClinicPatients(clinicId);

        // Metrics
        long totalAppointments = appointmentRepository.countByClinicId(clinicId);
        long todayAppointmentsCount = todayAppointments.size();
        long totalPatients = patients.size();
        long activeDoctorsCount = affiliatedDoctors.size();

        return ClinicDashboardDTO.builder()
                .profile(profile)
                .totalPatients(totalPatients)
                .todayAppointmentsCount(todayAppointmentsCount)
                .totalAppointments(totalAppointments)
                .activeDoctorsCount(activeDoctorsCount)
                .pendingAffiliationsCount(pendingAffiliations.size())
                .todayAppointments(todayAppointments)
                .upcomingAppointments(upcomingAppointments)
                .activeAffiliations(activeAffiliations)
                .pendingAffiliations(pendingAffiliations)
                .affiliatedDoctors(affiliatedDoctors)
                .recentPatients(patients.stream().limit(10).collect(Collectors.toList()))
                .build();
    }
}
