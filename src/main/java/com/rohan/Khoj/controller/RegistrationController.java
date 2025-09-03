package com.rohan.Khoj.controller;

import com.rohan.Khoj.dto.ClinicRegistrationRequestDTO;
import com.rohan.Khoj.dto.DoctorRegistrationRequestDTO;
import com.rohan.Khoj.dto.PatientRegistrationRequestDTO;
import com.rohan.Khoj.dto.RegistrationResponseDTO;
import com.rohan.Khoj.service.ClinicRegistrationService;
import com.rohan.Khoj.service.DoctorRegistrationService;
import com.rohan.Khoj.service.PatientRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Lombok annotation to generate constructor for final fields
public class RegistrationController {

    private final PatientRegistrationService patientRegistrationService;
    private final DoctorRegistrationService doctorRegistrationService;
    private final ClinicRegistrationService clinicRegistrationService;

    /**
     * Handles the registration request for a new patient.
     * The method is now much cleaner, as exception handling is delegated
     * to the GlobalExceptionHandler.
     *
     * @param request The patient registration data, validated.
     * @return A ResponseEntity with the success response DTO and a 201 CREATED status.
     */
    @PostMapping("/register/patient")
    public ResponseEntity<RegistrationResponseDTO> registerPatient(@Valid @RequestBody PatientRegistrationRequestDTO request) {
        // The service method is called directly.
        // If it succeeds, it returns the response DTO.
        // If it fails (e.g., duplicate user), it throws an exception which is
        // caught by the GlobalExceptionHandler, which then sends the appropriate error JSON.
        RegistrationResponseDTO response = patientRegistrationService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Handles the registration of a new doctor.
     * Validates the incoming request DTO and delegates to the doctor registration service.
     *
     * @param request The DoctorRegistrationRequestDTO containing doctor details.
     * @return ResponseEntity with RegistrationResponseDTO and appropriate HTTP status.
     */
    @PostMapping("/register/doctor")
    public ResponseEntity<RegistrationResponseDTO> registerDoctor(@Valid @RequestBody DoctorRegistrationRequestDTO request) {
        try {
            // The service method is expected to return RegistrationResponseDTO directly
            RegistrationResponseDTO response = doctorRegistrationService.registerDoctor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Conflict during doctor registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    RegistrationResponseDTO.builder().message(e.getMessage()).build()
            );
        } catch (Exception e) {
            System.err.println("Unexpected error during doctor registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RegistrationResponseDTO.builder().message("An unexpected error occurred during doctor registration. Please try again later.").build()
            );
        }
    }

    /**
     * Handles the registration of a new clinic.
     * Validates the incoming request DTO and delegates to the clinic registration service.
     *
     * @param request The ClinicRegistrationRequestDTO containing clinic details.
     * @return ResponseEntity with RegistrationResponseDTO and appropriate HTTP status.
     */
    @PostMapping("/register/clinic")
    public ResponseEntity<RegistrationResponseDTO> registerClinic(@Valid @RequestBody ClinicRegistrationRequestDTO request) {
        try {
            // The service method is expected to return RegistrationResponseDTO directly
            RegistrationResponseDTO response = clinicRegistrationService.registerClinic(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Conflict during clinic registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    RegistrationResponseDTO.builder().message(e.getMessage()).build()
            );
        } catch (Exception e) {
            System.err.println("Unexpected error during clinic registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RegistrationResponseDTO.builder().message("An unexpected error occurred during clinic registration. Please try again later.").build()
            );
        }
    }
}