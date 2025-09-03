package com.rohan.Khoj.service;

import com.rohan.Khoj.dto.PatientRegistrationRequestDTO;
import com.rohan.Khoj.dto.RegistrationResponseDTO;
import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.entity.Role;
import com.rohan.Khoj.entity.UserType;
import com.rohan.Khoj.customException.UserAlreadyExistsException; // Import the custom exception
import com.rohan.Khoj.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public PatientRegistrationService(PatientRepository patientRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrationResponseDTO registerPatient(PatientRegistrationRequestDTO request) {
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
        newPatient.setPhoneNumbers(new HashSet<>()); // Ensure collection is initialized

        // 5. Save the Entity
        PatientEntity savedPatient = patientRepository.save(newPatient);

        // 6. Map the saved Entity to the success response DTO
        return RegistrationResponseDTO.builder()
                .message("Patient registered successfully!")
                .id(savedPatient.getId())
                .username(savedPatient.getUsername())
                .registeredUserType(UserType.PATIENT)
                .build();
    }
}