package com.rohan.Khoj.service;

// Removed unused imports: ClinicModelMapperConfig, Autowired
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.DoctorDTO; // Response DTO
import com.rohan.Khoj.dto.DoctorUpdateRequestDTO; // Request DTO
import com.rohan.Khoj.dto.DoctorClinicAffiliationDTO; // New affiliation DTO
import com.rohan.Khoj.dto.ClinicDTO; // For getClinicsForDoctor response
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.repository.DoctorClinicAffiliationRepository;
import com.rohan.Khoj.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default for service methods
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final DoctorClinicAffiliationRepository affiliationRepository; // Injected via constructor
    private final ClinicService clinicService; // Injected via constructor

    // --- Update Operation ---

    /**
     * Updates an existing doctor's details based on the provided DTO.
     * Handles uniqueness checks for username, email, and medical license number, and password hashing.
     *
     * @param id The UUID of the doctor to update.
     * @param updateRequestDTO The DTO containing the updated doctor details.
     * @return The updated DoctorDto.
     * @throws ResourceNotFoundException if the doctor with the given ID is not found.
     * @throws ConflictException if username, email, or medical license number update conflicts with an existing user.
     */
    @Transactional // This operation modifies data
    public DoctorDTO updateDoctor(UUID id, DoctorUpdateRequestDTO updateRequestDTO) {
        DoctorEntity doctorToUpdate = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        // --- Handle Username Update ---
        if (updateRequestDTO.getUsername() != null && !updateRequestDTO.getUsername().equals(doctorToUpdate.getUsername())) {
            Optional<DoctorEntity> existingDoctorWithNewUsername = doctorRepository.findByUsername(updateRequestDTO.getUsername());
            if (existingDoctorWithNewUsername.isPresent() && !existingDoctorWithNewUsername.get().getId().equals(id)) {
                throw new ConflictException("Username '" + updateRequestDTO.getUsername() + "' is already taken by another doctor.");
            }
            doctorToUpdate.setUsername(updateRequestDTO.getUsername());
        }

        // --- Handle Email Update ---
        if (updateRequestDTO.getEmail() != null && !updateRequestDTO.getEmail().equals(doctorToUpdate.getEmailId())) {
            // Ensure DoctorRepository has 'Optional<DoctorEntity> findByEmailId(String emailId);'
            Optional<DoctorEntity> existingDoctorWithNewEmail = doctorRepository.findByEmailId(updateRequestDTO.getEmail());
            if (existingDoctorWithNewEmail.isPresent() && !existingDoctorWithNewEmail.get().getId().equals(id)) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmail() + "' is already in use by another doctor.");
            }
            doctorToUpdate.setEmailId(updateRequestDTO.getEmail());
        }

        // --- Handle Password Update ---
        if (updateRequestDTO.getPassword() != null && !updateRequestDTO.getPassword().isEmpty()) {
            doctorToUpdate.setPassword(passwordEncoder.encode(updateRequestDTO.getPassword())); // Corrected method name
        }

        // --- Handle Medical License Number Update ---
        // Assuming DoctorEntity's field name is 'medicalLicenseNumber' as in DTO
        if (updateRequestDTO.getRegistrationNumber() != null && !updateRequestDTO.getRegistrationNumber().equals(doctorToUpdate.getRegistrationNumber())) {
            Optional<DoctorEntity> existingDoctorWithNewLicense = doctorRepository.findByRegistrationNumber(updateRequestDTO.getRegistrationNumber());
            if (existingDoctorWithNewLicense.isPresent() && !existingDoctorWithNewLicense.get().getId().equals(id)) {
                throw new ConflictException("Medical license number '" + updateRequestDTO.getRegistrationNumber() + "' is already registered to another doctor.");
            }
            doctorToUpdate.setRegistrationNumber(updateRequestDTO.getRegistrationNumber()); // Corrected method name
        }

        // --- Map other fields using ModelMapper ---
        modelMapper.map(updateRequestDTO, doctorToUpdate);

        // Update updatedAt timestamp
        doctorToUpdate.setUpdatedAt(LocalDateTime.now());

        // Save the updated entity
        DoctorEntity updatedDoctorEntity = doctorRepository.save(doctorToUpdate);

        // Map the saved entity back to a DTO for the response
        return modelMapper.map(updatedDoctorEntity, DoctorDTO.class);
    }

    // --- Retrieval Operations (Returning DTOs) ---

    /**
     * Finds a doctor by their username.
     * @param username The username to search for.
     * @return An Optional containing the DoctorDto if found.
     */
    public Optional<DoctorDTO> getDoctorByUsername(String username) { // Renamed for consistency
        return doctorRepository.findByUsername(username)
                .map(doctorEntity -> modelMapper.map(doctorEntity, DoctorDTO.class));
    }

    // Add this helper method for internal service use
    /**
     * Finds a DoctorEntity by its ID. Used internally by other services when the entity itself is needed.
     *
     * @param id The UUID of the doctor.
     * @return An Optional containing the DoctorEntity if found.
     */
    public Optional<DoctorEntity> getDoctorEntityById(UUID id) {
        return doctorRepository.findById(id);
    }

    /**
     * Retrieves all doctors, mapped to DTOs.
     * @return A list of DoctorDto.
     */
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorEntity -> modelMapper.map(doctorEntity, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds a doctor by their ID, mapped to a DTO.
     * @param id The UUID of the doctor to search for.
     * @return An Optional containing the DoctorDto if found.
     */
    public Optional<DoctorDTO> getDoctorById(UUID id) {
        return doctorRepository.findById(id)
                .map(doctorEntity -> modelMapper.map(doctorEntity, DoctorDTO.class));
    }

    /**
     * Finds a doctor by their email ID, mapped to a DTO.
     * @param emailId The email ID to search for.
     * @return An Optional containing the DoctorDto if found.
     */
    public Optional<DoctorDTO> getDoctorByEmail(String emailId) { // Renamed for consistency
        return doctorRepository.findByEmailId(emailId)
                .map(doctorEntity -> modelMapper.map(doctorEntity, DoctorDTO.class));
    }

    /**
     * Finds doctors by specialization, mapped to DTOs.
     * @param specialization The specialization to search for.
     * @return A list of DoctorDto.
     */
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        // Assuming findBySpecializationsContaining is a correct repository method for a string
        return doctorRepository.findBySpecializationsContaining(specialization).stream()
                .map(doctorEntity -> modelMapper.map(doctorEntity, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds doctors by last name, mapped to DTOs.
     * @param lastName The last name to search for.
     * @return A list of DoctorDto.
     */
    public List<DoctorDTO> getDoctorsByLastName(String lastName) {
        // Assuming findByLastNameContaining is a correct repository method for a string
        return doctorRepository.findByLastNameContaining(lastName).stream()
                .map(doctorEntity -> modelMapper.map(doctorEntity, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    // --- Delete Operation ---

    /**
     * Deletes a doctor by their ID.
     * @param id The UUID of the doctor to delete.
     * @return The DoctorDto of the doctor that was deleted.
     * @throws ResourceNotFoundException if the doctor with the given ID is not found.
     */
    @Transactional // This operation modifies data
    public DoctorDTO deleteDoctor(UUID id) {
        DoctorEntity doctorToDelete = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctorRepository.delete(doctorToDelete); // Use delete(entity) for fewer queries

        return modelMapper.map(doctorToDelete, DoctorDTO.class); // Return the DTO of the deleted doctor
    }

    // --- Doctor-Clinic Affiliation Operations ---

    /**
     * Affiliates a doctor with a clinic.
     * @param doctorId The ID of the doctor.
     * @param clinicId The ID of the clinic.
     * @param joiningDate The date the doctor joined the clinic.
     * @param roleInClinic The role of the doctor in the clinic (e.g., "Full-time").
     * @param shiftDetails Details about the doctor's shifts at this clinic.
     * @param charge Doctor's per-consultation or hourly charge at this clinic.
     * @return The created DoctorClinicAffiliationDTO object.
     * @throws ResourceNotFoundException if doctor or clinic not found.
     * @throws ConflictException if doctor is already affiliated with the clinic.
     */
    @Transactional
    public DoctorClinicAffiliationDTO affiliateDoctorToClinic(UUID doctorId, UUID clinicId, LocalDate joiningDate, String roleInClinic, String shiftDetails, Double charge) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        // Use clinicService to get ClinicEntity directly for internal service use
        ClinicEntity clinic = clinicService.getClinicEntityById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + clinicId));

        if (affiliationRepository.existsByDoctorAndClinic(doctor, clinic)) {
            throw new ConflictException("Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " is already affiliated with clinic " + clinic.getName());
        }

        // Build the affiliation entity
        DoctorClinicAffiliationEntity affiliation = DoctorClinicAffiliationEntity.builder()
                .doctor(doctor)
                .clinic(clinic)
                .joiningDate(joiningDate)
                .roleInClinic(roleInClinic)
                .shiftDetails(shiftDetails)
                .charge(charge)
                .build();

        DoctorClinicAffiliationEntity savedAffiliation = affiliationRepository.save(affiliation);

        // Map and return the DTO
        return modelMapper.map(savedAffiliation, DoctorClinicAffiliationDTO.class);
    }

    /**
     * Removes a doctor's affiliation with a clinic.
     * @param doctorId The ID of the doctor.
     * @param clinicId The ID of the clinic.
     * @throws ResourceNotFoundException if doctor, clinic, or affiliation not found.
     */
    @Transactional
    public void removeDoctorAffiliation(UUID doctorId, UUID clinicId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        ClinicEntity clinic = clinicService.getClinicEntityById(clinicId) // Use clinicService to get ClinicEntity
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + clinicId));

        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findByDoctorAndClinic(doctor, clinic)
                .orElseThrow(() -> new ResourceNotFoundException("Affiliation not found between doctor " + doctorId + " and clinic " + clinicId));

        affiliationRepository.delete(affiliation);
    }

    /**
     * Get all clinics a specific doctor is affiliated with, mapped to Clinic DTOs.
     * @param doctorId The ID of the doctor.
     * @return A list of ClinicDto the doctor works at.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    public List<ClinicDTO> getClinicsForDoctor(UUID doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        return affiliationRepository.findByDoctor(doctor).stream()
                .map(DoctorClinicAffiliationEntity::getClinic)
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class)) // Map ClinicEntity to ClinicDto
                .collect(Collectors.toList());
    }

    // You might also want a method to get a specific affiliation detail
    public Optional<DoctorClinicAffiliationDTO> getDoctorClinicAffiliation(UUID affiliationId) {
        return affiliationRepository.findById(affiliationId)
                .map(affiliationEntity -> modelMapper.map(affiliationEntity, DoctorClinicAffiliationDTO.class));
    }
}