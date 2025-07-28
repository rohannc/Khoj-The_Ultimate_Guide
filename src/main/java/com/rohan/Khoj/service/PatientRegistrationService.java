package com.rohan.Khoj.service;

import com.rohan.Khoj.dto.PatientRegistrationRequestDTO;
import com.rohan.Khoj.dto.RegistrationResponseDTO;
import com.rohan.Khoj.dto.MobileNumberWrapperDTO; // Import wrapper DTO
import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.entity.Role;
import com.rohan.Khoj.entity.UserType; // Assuming you have this enum
import com.rohan.Khoj.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet; // For initializing collections
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientRegistrationService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PatientRegistrationService(PatientRepository patientRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrationResponseDTO registerPatient(PatientRegistrationRequestDTO request) {
        // 1. Validate for uniqueness before mapping (optional, but can give earlier feedback)
        if (patientRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (patientRepository.existsByEmailId(request.getEmailId())) {
            throw new IllegalArgumentException("Email '" + request.getEmailId() + "' is already registered.");
        }

        // 2. Map DTO to Entity
        // ModelMapper will handle direct field mapping and the phoneNumbers conversion
        PatientEntity newPatient = modelMapper.map(request, PatientEntity.class);

        // 3. Handle password hashing (CRITICAL security step)
        newPatient.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. Set system-generated fields (if not handled by @PrePersist in BaseUserEntity)
        if (newPatient.getCreatedAt() == null) {
            newPatient.setCreatedAt(LocalDateTime.now());
        }

        if (newPatient.getUpdatedAt() == null) {
            newPatient.setUpdatedAt(newPatient.getCreatedAt());
        }

        if (newPatient.getRole() == null) {
            newPatient.setRole(Role.ROLE_PATIENT);
        }

        // Ensure phoneNumbers set is initialized if not done by SuperBuilder/Lombok
        if (newPatient.getPhoneNumbers() == null) {
            newPatient.setPhoneNumbers(new HashSet<>());
        }


        // 5. Save the Entity to the database
        PatientEntity savedPatient = patientRepository.save(newPatient);

        // 6. Map the saved Entity to the RegistrationResponseDTO
        return RegistrationResponseDTO.builder()
                .message("Patient registered successfully!")
                .id(savedPatient.getId()) // Use 'id' from the saved entity
                .username(savedPatient.getUsername()) // Use 'username' from the saved entity
                .registeredUserType(UserType.PATIENT)
                .build();
    }
}