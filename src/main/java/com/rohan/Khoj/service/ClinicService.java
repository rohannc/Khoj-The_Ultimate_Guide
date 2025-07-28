package com.rohan.Khoj.service;

// Removed unused imports: ClinicModelMapperConfig, Autowired
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.ClinicDTO; // Response DTO
import com.rohan.Khoj.dto.ClinicUpdateRequestDTO; // Request DTO
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.repository.ClinicRepository;
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
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // --- Update Operation ---

    /**
     * Updates an existing clinic's details based on the provided DTO.
     * Handles uniqueness checks for username, email, and clinic name, and password hashing.
     *
     * @param id The UUID of the clinic to update.
     * @param updateRequestDTO The DTO containing the updated clinic details.
     * @return The updated ClinicDto.
     * @throws ResourceNotFoundException if the clinic with the given ID is not found.
     * @throws ConflictException if username, email, or clinic name update conflicts with an existing entry.
     */
    @Transactional // This operation modifies data
    public ClinicDTO updateClinic(UUID id, ClinicUpdateRequestDTO updateRequestDTO) {
        ClinicEntity clinicToUpdate = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        // --- Handle Username Update ---
        if (updateRequestDTO.getUsername() != null && !updateRequestDTO.getUsername().equals(clinicToUpdate.getUsername())) {
            Optional<ClinicEntity> existingClinicWithNewUsername = clinicRepository.findByUsername(updateRequestDTO.getUsername());
            if (existingClinicWithNewUsername.isPresent() && !existingClinicWithNewUsername.get().getId().equals(id)) {
                throw new ConflictException("Username '" + updateRequestDTO.getUsername() + "' is already taken by another clinic.");
            }
            clinicToUpdate.setUsername(updateRequestDTO.getUsername());
        }

        // --- Handle Email Update ---
        if (updateRequestDTO.getEmail() != null && !updateRequestDTO.getEmail().equals(clinicToUpdate.getEmailId())) {
            // IMPORTANT: ClinicRepository must have 'Optional<ClinicEntity> findByEmailId(String emailId);'
            Optional<ClinicEntity> existingClinicWithNewEmail = clinicRepository.findByEmailId(updateRequestDTO.getEmail());
            if (existingClinicWithNewEmail.isPresent() && !existingClinicWithNewEmail.get().getId().equals(id)) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmail() + "' is already in use by another clinic.");
            }
            clinicToUpdate.setEmailId(updateRequestDTO.getEmail());
        }

        // --- Handle Password Update ---
        if (updateRequestDTO.getPassword() != null && !updateRequestDTO.getPassword().isEmpty()) {
            clinicToUpdate.setPassword(passwordEncoder.encode(updateRequestDTO.getPassword())); // Corrected method name
        }

        // --- Handle Clinic Name Update ---
        if (updateRequestDTO.getName() != null && !updateRequestDTO.getName().equals(clinicToUpdate.getName())) {
            Optional<ClinicEntity> existingClinicWithNewName = clinicRepository.findByName(updateRequestDTO.getName());
            if (existingClinicWithNewName.isPresent() && !existingClinicWithNewName.get().getId().equals(id)) { // Added check
                throw new ConflictException("Clinic with name '" + updateRequestDTO.getName() + "' already exists."); // Added throw
            }
            clinicToUpdate.setName(updateRequestDTO.getName());
        }

        // --- Map other fields using ModelMapper ---
        modelMapper.map(updateRequestDTO, clinicToUpdate);

        // Update updatedAt timestamp
        clinicToUpdate.setUpdatedAt(LocalDateTime.now());

        // Save the updated entity
        ClinicEntity updatedClinicEntity = clinicRepository.save(clinicToUpdate);

        // Map the saved entity back to a DTO for the response
        return modelMapper.map(updatedClinicEntity, ClinicDTO.class);
    }

    // --- Retrieval Operations (Returning DTOs) ---

    /**
     * Finds a clinic by their username.
     * @param username The username to search for.
     * @return An Optional containing the ClinicDto if found.
     */
    public Optional<ClinicDTO> getClinicByUsername(String username) { // Renamed from findByUsername (in signature)
        return clinicRepository.findByUsername(username)
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class));
    }

    /**
     * Finds a ClinicEntity by its ID. Used internally by other services when the entity itself is needed.
     *
     * @param id The UUID of the clinic.
     * @return An Optional containing the ClinicEntity if found.
     */
    public Optional<ClinicEntity> getClinicEntityById(UUID id) {
        return clinicRepository.findById(id);
    }

    /**
     * Retrieves all clinics, mapped to DTOs.
     * @return A list of ClinicDto.
     */
    public List<ClinicDTO> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds a clinic by its ID, mapped to a DTO.
     * @param id The UUID of the clinic to search for.
     * @return An Optional containing the ClinicDto if found.
     */
    public Optional<ClinicDTO> getClinicById(UUID id) {
        return clinicRepository.findById(id)
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class));
    }

    /**
     * Finds a clinic by its name, mapped to a DTO.
     * @param name The name of the clinic to search for.
     * @return An Optional containing the ClinicDto if found.
     */
    public Optional<ClinicDTO> getClinicByName(String name) {
        return clinicRepository.findByName(name)
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class));
    }

    /**
     * Finds a clinic by its email ID, mapped to a DTO.
     * @param emailId The email ID to search for.
     * @return An Optional containing the ClinicDto if found.
     */
    public Optional<ClinicDTO> getClinicByEmail(String emailId) {
        return clinicRepository.findByEmailId(emailId)
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class));
    }

    /**
     * Finds clinics by city, mapped to DTOs.
     * @param city The city to search for.
     * @return A list of ClinicDto.
     */
    public List<ClinicDTO> getClinicsByCity(String city) {
        return clinicRepository.findByCity(city).stream()
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Finds clinics by pin code, mapped to DTOs.
     * @param pinCode The pin code to search for.
     * @return A list of ClinicDto.
     */
    public List<ClinicDTO> getClinicsByPinCode(String pinCode) {
        return clinicRepository.findByPinCode(pinCode).stream()
                .map(clinicEntity -> modelMapper.map(clinicEntity, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    // --- Delete Operation ---

    /**
     * Deletes a clinic by its ID.
     *
     * @param id The UUID of the clinic to delete.
     * @return The ClinicDto of the clinic that was deleted.
     * @throws ResourceNotFoundException if the clinic with the given ID is not found.
     */
    @Transactional // This operation modifies data
    public ClinicDTO deleteClinic(UUID id) {
        ClinicEntity clinicToDelete = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        clinicRepository.delete(clinicToDelete); // Use delete(entity) for fewer queries

        return modelMapper.map(clinicToDelete, ClinicDTO.class); // Return the DTO of the deleted clinic
    }
}