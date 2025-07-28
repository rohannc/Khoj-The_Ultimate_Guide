package com.rohan.Khoj.service;

// Corrected DTO imports
import com.rohan.Khoj.dto.ClinicRegistrationRequestDTO;
import com.rohan.Khoj.dto.RegistrationResponseDTO;
import com.rohan.Khoj.dto.MobileNumberWrapperDTO; // Import wrapper DTO
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.Role; // Assuming this enum is defined
import com.rohan.Khoj.entity.UserType; // Assuming this enum is defined
import com.rohan.Khoj.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper; // Correctly import ModelMapper
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicRegistrationService {

    private final ClinicRepository clinicRepository;
    private final ModelMapper modelMapper; // Inject ModelMapper bean directly
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegistrationResponseDTO registerClinic(ClinicRegistrationRequestDTO request) { // Updated DTO name and return type
        // 1. Perform uniqueness checks early for better feedback
        if (clinicRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (clinicRepository.existsByEmailId(request.getEmailId())) { // Corrected to request.getEmail()
            throw new IllegalArgumentException("Email '" + request.getEmailId() + "' is already registered.");
        }

        // 2. Map DTO to Entity using ModelMapper
        // will handle direct field mappings and phone numbers conversion.
        ClinicEntity newClinic = modelMapper.map(request, ClinicEntity.class);

        // 3. Handle password hashing (CRITICAL security step)
        newClinic.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Set system-generated fields (if not handled by @PrePersist in BaseUserEntity)
        if (newClinic.getCreatedAt() == null) {
            newClinic.setCreatedAt(LocalDateTime.now());
        }

        if (newClinic.getUpdatedAt() == null) {
            newClinic.setUpdatedAt(newClinic.getCreatedAt());
        }

        if (newClinic.getRole() == null) {
            newClinic.setRole(Role.ROLE_CLINIC);
        }

        // 5. Ensure collections are initialized if ModelMapper/Lombok builder doesn't guarantee it
        if (newClinic.getPhoneNumbers() == null) {
            newClinic.setPhoneNumbers(new HashSet<>());
        }
        if (newClinic.getOpeningHours() == null) {
            newClinic.setOpeningHours(new HashMap<>());
        }
        // Other collections like doctorAffiliations, appointments will be managed separately

        System.out.println("Attempting to register new clinic: " + request.getUsername());

        try {
            // 6. Save the entity
            ClinicEntity savedClinic = clinicRepository.save(newClinic);
            System.out.println("Successfully registered clinic: " + savedClinic.getUsername() + " (ID: " + savedClinic.getId() + ")");

            // 7. Construct and return the RegistrationResponseDTO
            return RegistrationResponseDTO.builder()
                    .message("Clinic registered successfully!")
                    .id(savedClinic.getId()) // Use 'id' from the saved entity
                    .username(savedClinic.getUsername()) // Use 'username' from the saved entity
                    .registeredUserType(UserType.CLINIC) // Set the user type
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to register clinic " + request.getUsername() + ": " + e.getMessage());
            // Re-throw as a more specific exception if needed, or a generic RuntimeException
            throw new RuntimeException("Error saving clinic during registration: " + e.getMessage(), e);
        }
    }
}