package com.rohan.Khoj.auth;

import com.rohan.Khoj.auth.DoctorRegistrationRequestDTO; // Updated DTO name
import com.rohan.Khoj.auth.AuthResponseDTO;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.common.Role; // Assuming this enum is defined
import com.rohan.Khoj.common.UserType; // Assuming this enum is defined
import com.rohan.Khoj.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper; // Import ModelMapper
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet; // For initializing collections if needed

import com.rohan.Khoj.config.ModelMapperConfig;
import com.rohan.Khoj.common.BaseUserEntity;
import com.rohan.Khoj.security.JwtService;

@Service
@RequiredArgsConstructor // Generates constructor for final fields
public class DoctorRegistrationService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper; // Inject ModelMapper
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // No need for UserService.isUsernameTaken() if DoctorRepository can check username directly.
    // If username uniqueness is across all user types (Patient, Doctor, Clinic),
    // then a shared UserService or a custom Spring Data JPA validator would be better.
    // For now, assuming uniqueness check is within DoctorRepository.

    @Transactional
    public AuthResponseDTO registerDoctor(DoctorRegistrationRequestDTO request) { // Updated DTO name and return type
        // 1. Perform uniqueness checks early for better feedback
        if (doctorRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (doctorRepository.existsByEmailId(request.getEmailId())) {
            throw new IllegalArgumentException("Email '" + request.getEmailId() + "' is already registered.");
        }
        if (request.getRegistrationNumber() != null && doctorRepository.findByRegistrationNumber(request.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("Medical license number '" + request.getRegistrationNumber() + "' is already registered.");
        }

        // 2. Map DTO to Entity using ModelMapper
        // ModelMapper will handle most direct field mappings (firstName, lastName, gender, specialization,
        // qualifications, experienceYears, registrationIssueDate, medicalLicenseNumber, phoneNumbers).
        // It also handles username and email to emailId based on your ModelMapperConfig.
        DoctorEntity newDoctor = modelMapper.map(request, DoctorEntity.class);

        // 3. Handle password hashing (CRITICAL security step)
        newDoctor.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Set system-generated fields (if not handled by @PrePersist in BaseUserEntity)
        if (newDoctor.getCreatedAt() == null) {
            newDoctor.setCreatedAt(LocalDateTime.now());
        }

        if (newDoctor.getUpdatedAt() == null) {
            newDoctor.setUpdatedAt(newDoctor.getCreatedAt());
        }

        if (newDoctor.getRole() == null) {
            newDoctor.setRole(Role.ROLE_DOCTOR);
        }

        // If DoctorEntity has other collections like clinicAffiliations or appointments,
        // ensure they are initialized if they might be null after mapping.
        // newDoctor.setClinicAffiliations(new HashSet<>());
        // newDoctor.setAppointments(new HashSet<>());


        System.out.println("Attempting to register new doctor: " + request.getUsername());

        try {
            // 5. Save the Entity to the database
            DoctorEntity savedDoctor = doctorRepository.save(newDoctor);
            System.out.println("Successfully registered doctor: " + savedDoctor.getUsername() + " (ID: " + savedDoctor.getId() + ")");

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);

            // 6. Construct and return the AuthResponseDTO
            return AuthResponseDTO.builder()
                    .message("Doctor registered successfully!")
                    .userId(savedDoctor.getId()) // Use 'id' from the saved entity
                    .username(savedDoctor.getUsername()) // Use 'username' from the saved entity
                    .userType(UserType.DOCTOR) // Set the user type
                    .token(token)
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to register doctor " + request.getUsername() + ": " + e.getMessage());
            // Re-throw as a more specific exception if needed, or a generic RuntimeException
            throw new RuntimeException("Error saving doctor during registration: " + e.getMessage(), e);
        }
    }
}