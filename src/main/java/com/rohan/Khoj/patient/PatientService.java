package com.rohan.Khoj.patient;

import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.exception.ConflictException;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.patient.PatientDTO;
import com.rohan.Khoj.patient.PatientUpdateRequestDTO;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import com.rohan.Khoj.common.PasswordUpdateRequestDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default for service methods
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // --- Update Operations ---

    /**
     * Checks if a username is available. If not, generates up to 3 available suggestions.
     */
    public UsernameAvailabilityResponseDTO checkUsernameAvailability(String requestedUsername) {
        boolean isAvailable = !patientRepository.existsByUsername(requestedUsername);
        List<String> suggestions = new ArrayList<>();

        if (!isAvailable) {
            suggestions = generateUsernameSuggestions(requestedUsername);
        }

        return new UsernameAvailabilityResponseDTO(isAvailable, suggestions);
    }

    private List<String> generateUsernameSuggestions(String baseUsername) {
        List<String> suggestions = new ArrayList<>();
        Random random = new Random();
        int attempts = 0;
        int currentYear = LocalDateTime.now().getYear();

        String[] prefixes = {"user_", "the_", "iam_"};
        
        // 1. Try appending the current year
        String yearSuggestion = baseUsername + currentYear;
        if (!patientRepository.existsByUsername(yearSuggestion)) {
            suggestions.add(yearSuggestion);
        }

        // 2. Try random numbers
        while (suggestions.size() < 3 && attempts < 20) {
            String randomNumSuggestion = baseUsername + (100 + random.nextInt(900)); // 3-digit random
            if (!suggestions.contains(randomNumSuggestion) && !patientRepository.existsByUsername(randomNumSuggestion)) {
                suggestions.add(randomNumSuggestion);
            }
            attempts++;
        }

        // 3. Try prefixes if we still need more
        for (String prefix : prefixes) {
            if (suggestions.size() >= 3) break;
            String prefixSuggestion = prefix + baseUsername;
            if (!suggestions.contains(prefixSuggestion) && !patientRepository.existsByUsername(prefixSuggestion)) {
                suggestions.add(prefixSuggestion);
            }
        }

        return suggestions.size() > 3 ? suggestions.subList(0, 3) : suggestions;
    }

    @Transactional
    public void updateUsername(UUID id, UsernameUpdateRequestDTO request) {
        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        if (patient.getUsername().equals(request.getNewUsername())) {
            return; // No change needed
        }

        if (patientRepository.existsByUsername(request.getNewUsername())) {
            throw new ConflictException("Username '" + request.getNewUsername() + "' is already taken.");
        }

        patient.setUsername(request.getNewUsername());
        patient.setUpdatedAt(LocalDateTime.now());
        patientRepository.save(patient);
    }

    // --- Update Patient Details ---

    /**
     * Updates an existing patient's details based on the provided DTO.
     * Handles uniqueness checks for username and email. Password updates are handled separately.
     *
     * @param id The UUID of the patient to update.
     * @param updateRequestDTO The DTO containing the updated patient details.
     * @return The updated PatientDTO.
     * @throws ResourceNotFoundException if the patient with the given ID is not found.
     * @throws ConflictException if username or email update conflicts with an existing user.
     */
    @Transactional // This operation modifies data
    public PatientDTO updatePatient(UUID id, PatientUpdateRequestDTO updateRequestDTO) {
        PatientEntity patientToUpdate = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // --- Map only the non-null fields from the DTO onto the existing Entity ---
        ModelMapper patchMapper = new ModelMapper();
        patchMapper.getConfiguration().setSkipNullEnabled(true);
        patchMapper.map(updateRequestDTO, patientToUpdate);

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
     * @return An Optional containing the PatientDTO if found.
     */
    public Optional<PatientDTO> getPatientByUsername(String username) {
        return patientRepository.findByUsername(username)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
    }

    /**
     * Retrieves all patients, mapped to DTOs.
     *
     * @return A list of PatientDTO.
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
     * @return An Optional containing the PatientDTO if found.
     */
    public Optional<PatientDTO> getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
    }

    public Optional<PatientEntity> getPatientEntityById(UUID id) {
        return patientRepository.findById(id);
    }

    /**
     * Finds a patient by their email ID, mapped to a DTO.
     *
     * @param emailId The email ID to search for.
     * @return An Optional containing the PatientDTO if found.
     */
    public Optional<PatientDTO> getPatientByEmail(String emailId) {
        return patientRepository.findByEmailId(emailId)
                .map(patientEntity -> modelMapper.map(patientEntity, PatientDTO.class));
    }

    /**
     * Finds patients by city, mapped to DTOs.
     *
     * @param city The city to search for.
     * @return A list of PatientDTO.
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
     * @return A list of PatientDTO.
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
