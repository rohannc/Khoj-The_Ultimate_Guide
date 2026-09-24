package com.rohan.Khoj.auth;

import com.rohan.Khoj.auth.PatientRegistrationRequestDTO;
import com.rohan.Khoj.auth.AuthResponseDTO;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.common.Role;
import com.rohan.Khoj.common.UserType;
import com.rohan.Khoj.exception.UserAlreadyExistsException; // Import the custom exception
import com.rohan.Khoj.security.JwtService;
import com.rohan.Khoj.patient.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class PatientRegistrationService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    public final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Our custom UserDetailsService

    @Autowired
    public PatientRegistrationService(PatientRepository patientRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, JwtService jwtService, UserDetailsService userDetailsService) {
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponseDTO registerPatient(PatientRegistrationRequestDTO request) {
        // 1. Validate for uniqueness. Throw a specific exception if a user exists.
        if (patientRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (patientRepository.existsByEmailId(request.getEmailId())) {
            throw new UserAlreadyExistsException("Email '" + request.getEmailId() + "' is already registered.");
        }

        // 2. Map DTO to Entity
        PatientEntity newPatient = modelMapper.map(request, PatientEntity.class);

        // 3. Handle password hashing
        newPatient.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Set system-generated fields
        newPatient.setCreatedAt(LocalDateTime.now());
        newPatient.setUpdatedAt(newPatient.getCreatedAt());
        newPatient.setRole(Role.ROLE_PATIENT);

        // 5. Save the Entity
        PatientEntity savedPatient = patientRepository.save(newPatient);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        // 6. Map the saved Entity to the success response DTO
        return AuthResponseDTO.builder()
                .message("Patient registered successfully!")
                .userId(savedPatient.getId())
                .username(savedPatient.getUsername())
                .userType(UserType.PATIENT)
                .token(token)
                .build();
    }
}