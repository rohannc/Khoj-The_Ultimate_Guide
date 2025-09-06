package com.rohan.Khoj.service;

import com.rohan.Khoj.customException.BadRequestException;
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.PatientDTO;
import com.rohan.Khoj.dto.PatientUpdateRequestDTO;
import com.rohan.Khoj.dto.update.PasswordUpdateRequestDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default for service methods
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // --- Update Operations ---

    /**
     * Updates an existing patient's details based on the provided DTO.
     * Handles uniqueness checks for username and email. Password updates are handled separately.
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
            Optional<PatientEntity> existingPatientWithNewEmail = patientRepository.findByEmailId(updateRequestDTO.getEmail());
            if (existingPatientWithNewEmail.isPresent() && !existingPatientWithNewEmail.get().getId().equals(id)) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmail() + "' is already in use by another patient.");
            }
            patientToUpdate.setEmailId(updateRequestDTO.getEmail());
        }

        // --- Map other fields using ModelMapper (Password is explicitly excluded) ---
        modelMapper.map(updateRequestDTO, patientToUpdate);

        // Update updatedAt timestamp
        patientToUpdate.setUpdatedAt(LocalDateTime.now());

        // Save the updated entity
        PatientEntity updatedPatientEntity = patientRepository.save(patientToUpdate);

        // Map the saved entity back to a DTO for the response
        return modelMapper.map(updatedPatientEntity, PatientDTO.class);
    }

    /**
     * Updates the password for a specific patient.
     * Verifies the current password before setting the new one.
     *
     * @param id The UUID of the patient.
     * @param passwordRequest The DTO containing the current and new passwords.
     * @throws ResourceNotFoundException if the patient is not found.
     * @throws BadRequestException if the current password does not match.
     */
    @Transactional
    public void updatePassword(UUID id, PasswordUpdateRequestDTO passwordRequest) {
        PatientEntity patientToUpdate = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // 1. Verify the current password
        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), patientToUpdate.getPassword())) {
            throw new BadRequestException("Incorrect current password.");
        }

        // 2. Encode and set the new password
        patientToUpdate.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));

        // 3. Update timestamp
        patientToUpdate.setUpdatedAt(LocalDateTime.now());

        // 4. Save the entity
        patientRepository.save(patientToUpdate);
    }

    // --- Retrieval Operations (Returning DTOs) ---

    /**
     * Finds a patient by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the PatientDto if found.
     */
    public Optional<PatientDTO> getPatientByUsername(String username) {
        return patientRepository.findByUsername(username)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
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
     * @throws ResourceNotFoundException if the patient with the given ID is not found.
     */
    @Transactional // This operation modifies data
    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
