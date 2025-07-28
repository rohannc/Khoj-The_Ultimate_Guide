package com.rohan.Khoj.service;

// Removed unused imports: DoctorEntity, DoctorRepository, ClinicModelMapperConfig, Autowired
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.PatientDTO; // Corrected DTO name to PatientDto for response
import com.rohan.Khoj.dto.PatientUpdateRequestDTO;
import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors; // For stream operations

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default for service methods
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // --- Update Operation ---

    /**
     * Updates an existing patient's details based on the provided DTO.
     * Handles uniqueness checks for username and email, and password hashing.
     *
     * @param id The UUID of the patient to update.
     * @param updateRequestDTO The DTO containing the updated patient details.
     * @return The updated PatientDto.
     * @throws ResourceNotFoundException if the patient with the given ID is not found.
     * @throws ConflictException if username or email update conflicts with an existing user.
     */
    @Transactional // This operation modifies data
    public PatientDTO updatePatient(UUID id, PatientUpdateRequestDTO updateRequestDTO) {
        PatientEntity patientToUpdate = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // --- Handle Username Update ---
        if (updateRequestDTO.getUsername() != null && !updateRequestDTO.getUsername().equals(patientToUpdate.getUsername())) {
            Optional<PatientEntity> existingPatientWithNewUsername = patientRepository.findByUsername(updateRequestDTO.getUsername());
            if (existingPatientWithNewUsername.isPresent() && !existingPatientWithNewUsername.get().getId().equals(id)) {
                throw new ConflictException("Username '" + updateRequestDTO.getUsername() + "' is already taken by another patient.");
            }
            patientToUpdate.setUsername(updateRequestDTO.getUsername());
        }

        // --- Handle Email Update ---
        if (updateRequestDTO.getEmail() != null && !updateRequestDTO.getEmail().equals(patientToUpdate.getEmailId())) {
            // Ensure PatientRepository has 'Optional<PatientEntity> findByEmailId(String emailId);'
            Optional<PatientEntity> existingPatientWithNewEmail = patientRepository.findByEmailId(updateRequestDTO.getEmail());
            if (existingPatientWithNewEmail.isPresent() && !existingPatientWithNewEmail.get().getId().equals(id)) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmail() + "' is already in use by another patient.");
            }
            patientToUpdate.setEmailId(updateRequestDTO.getEmail());
        }

        // --- Handle Password Update ---
        if (updateRequestDTO.getPassword() != null && !updateRequestDTO.getPassword().isEmpty()) {
            patientToUpdate.setPassword(passwordEncoder.encode(updateRequestDTO.getPassword())); // Corrected method name
        }

        // --- Map other fields using ModelMapper ---
        modelMapper.map(updateRequestDTO, patientToUpdate);

        // Update updatedAt timestamp
        patientToUpdate.setUpdatedAt(LocalDateTime.now());

        // Save the updated entity
        PatientEntity updatedPatientEntity = patientRepository.save(patientToUpdate);

        // Map the saved entity back to a DTO for the response
        return modelMapper.map(updatedPatientEntity, PatientDTO.class);
    }

    // --- Retrieval Operations (Returning DTOs) ---

    /**
     * Finds a patient by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the PatientDto if found.
     */
    public Optional<PatientDTO> getPatientByUsername(String username) { // Renamed from findByPatientname
        return patientRepository.findByUsername(username)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
    }

    /**
     * Finds a PatientEntity by its ID. Used internally by other services when the entity itself is needed.
     *
     * @param id The UUID of the patient.
     * @return An Optional containing the PatientEntity if found.
     */
    public Optional<PatientEntity> getPatientEntityById(UUID id) {
        return patientRepository.findById(id);
    }

    /**
     * Retrieves all patients, mapped to DTOs.
     *
     * @return A list of PatientDto.
     */
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds a patient by their ID, mapped to a DTO.
     *
     * @param id The UUID of the patient to search for.
     * @return An Optional containing the PatientDto if found.
     */
    public Optional<PatientDTO> getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
    }

    /**
     * Finds a patient by their email ID, mapped to a DTO.
     *
     * @param emailId The email ID to search for.
     * @return An Optional containing the PatientDto if found.
     */
    public Optional<PatientDTO> getPatientByEmail(String emailId) {
        return patientRepository.findByEmailId(emailId)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
    }

    /**
     * Finds patients by city, mapped to DTOs.
     *
     * @param city The city to search for.
     * @return A list of PatientDto.
     */
    public List<PatientDTO> getPatientsByCity(String city) {
        return patientRepository.findByCity(city).stream()
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds patients by blood group, mapped to DTOs.
     *
     * @param bloodGroup The blood group to search for.
     * @return A list of PatientDto.
     */
    public List<PatientDTO> getPatientsByBloodGroup(String bloodGroup) {
        return patientRepository.findByBloodGroup(bloodGroup).stream()
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class))
                .collect(Collectors.toList());
    }

    // --- Delete Operation ---

    /**
     * Deletes a patient by their ID.
     *
     * @param id The UUID of the patient to delete.
     * @return The PatientDto of the patient that was deleted.
     * @throws ResourceNotFoundException if the patient with the given ID is not found.
     */
    @Transactional // This operation modifies data
    public PatientDTO deletePatient(UUID id) {
        PatientEntity patientToDelete = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patientRepository.delete(patientToDelete); // Use delete(entity) for fewer queries

        return modelMapper.map(patientToDelete, PatientDTO.class); // Return the DTO of the deleted patient
    }

    // Note: The initial 'registerPatient' method is handled by PatientRegistrationService.
    // This PatientService focuses on post-registration CRUD and retrieval.
}