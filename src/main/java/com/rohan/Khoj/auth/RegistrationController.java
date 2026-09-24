package com.rohan.Khoj.auth;

import com.rohan.Khoj.auth.ClinicRegistrationRequestDTO;
import com.rohan.Khoj.auth.DoctorRegistrationRequestDTO;
import com.rohan.Khoj.auth.PatientRegistrationRequestDTO;
import com.rohan.Khoj.auth.AuthResponseDTO;
import com.rohan.Khoj.auth.ClinicRegistrationService;
import com.rohan.Khoj.auth.DoctorRegistrationService;
import com.rohan.Khoj.auth.PatientRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.rohan.Khoj.exception.GlobalExceptionHandler;
import com.rohan.Khoj.exception.UserAlreadyExistsException;

/**
 * Handles all user and entity registration endpoints.
 * This controller leverages a GlobalExceptionHandler to manage all error responses,
 * resulting in cleaner and more consistent code.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final PatientRegistrationService patientRegistrationService;
    private final DoctorRegistrationService doctorRegistrationService;
    private final ClinicRegistrationService clinicRegistrationService;

    /**
     * Handles the registration request for a new patient.
     * The service layer is responsible for business logic and throwing exceptions
     * on failure (e.g., UserAlreadyExistsException), which are handled globally.
     *
     * @param request The patient registration data, which is automatically validated.
     * @return A ResponseEntity with the success response DTO and a 201 CREATED status.
     */
    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponseDTO> registerPatient(@Valid @RequestBody PatientRegistrationRequestDTO request) {
        AuthResponseDTO response = patientRegistrationService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Handles the registration of a new doctor.
     * All try-catch blocks are removed, as exceptions are now centrally managed by the GlobalExceptionHandler.
     * The service should throw a specific custom exception (e.g., UserAlreadyExistsException) on conflict.
     *
     * @param request The DoctorRegistrationRequestDTO containing doctor details.
     * @return ResponseEntity with RegistrationResponseDTO and a 201 CREATED status.
     */
    @PostMapping("/register/doctor")
    public ResponseEntity<AuthResponseDTO> registerDoctor(@Valid @RequestBody DoctorRegistrationRequestDTO request) {
        AuthResponseDTO response = doctorRegistrationService.registerDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Handles the registration of a new clinic.
     * This follows the same clean pattern: delegate to the service and let the global handler
     * manage any potential errors like conflicts or validation failures.
     *
     * @param request The ClinicRegistrationRequestDTO containing clinic details.
     * @return ResponseEntity with RegistrationResponseDTO and a 201 CREATED status.
     */
    @PostMapping("/register/clinic")
    public ResponseEntity<AuthResponseDTO> registerClinic(@Valid @RequestBody ClinicRegistrationRequestDTO request) {
        AuthResponseDTO response = clinicRegistrationService.registerClinic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
