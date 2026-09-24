package com.rohan.Khoj.auth;

// Corrected DTO imports
import com.rohan.Khoj.auth.ClinicRegistrationRequestDTO;
import com.rohan.Khoj.auth.AuthResponseDTO;
import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.common.Role; // Assuming this enum is defined
import com.rohan.Khoj.common.UserType; // Assuming this enum is defined
import com.rohan.Khoj.clinic.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper; // Correctly import ModelMapper
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.HashMap;

import com.rohan.Khoj.common.BaseUserEntity;
import com.rohan.Khoj.security.JwtService;

@Service
@RequiredArgsConstructor
public class ClinicRegistrationService {

    private final ClinicRepository clinicRepository;
    private final ModelMapper modelMapper; // Inject ModelMapper bean directly
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponseDTO registerClinic(ClinicRegistrationRequestDTO request) { // Updated DTO name and return type
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

        // Other collections like doctorAffiliations, appointments will be managed separately

        System.out.println("Attempting to register new clinic: " + request.getUsername());

        try {
            // 6. Save the entity
            ClinicEntity savedClinic = clinicRepository.save(newClinic);
            System.out.println("Successfully registered clinic: " + savedClinic.getUsername() + " (ID: " + savedClinic.getId() + ")");

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);

            // 7. Construct and return the AuthResponseDTO
            return AuthResponseDTO.builder()
                    .message("Clinic registered successfully!")
                    .userId(savedClinic.getId()) // Use 'id' from the saved entity
                    .username(savedClinic.getUsername()) // Use 'username' from the saved entity
                    .userType(UserType.CLINIC) // Set the user type
                    .token(token)
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to register clinic " + request.getUsername() + ": " + e.getMessage());
            // Re-throw as a more specific exception if needed, or a generic RuntimeException
            throw new RuntimeException("Error saving clinic during registration: " + e.getMessage(), e);
        }
    }
}