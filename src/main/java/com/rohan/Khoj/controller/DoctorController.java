package com.rohan.Khoj.controller;

import com.rohan.Khoj.dto.DoctorDTO;
import com.rohan.Khoj.dto.DoctorUpdateRequestDTO;
import com.rohan.Khoj.dto.update.MessageResponseDTO;
import com.rohan.Khoj.dto.update.PasswordUpdateRequestDTO;
import com.rohan.Khoj.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing doctor-related operations.
 * This controller delegates all exception handling to the GlobalExceptionHandler for consistency.
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Retrieves all doctors. Returns an empty list if none are found.
     *
     * @return ResponseEntity with a list of DoctorDTOs and HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * Retrieves a specific doctor by their unique ID.
     *
     * @param id The UUID of the doctor.
     * @return ResponseEntity with the DoctorDTO and HTTP 200 OK.
     * @throws com.rohan.Khoj.customException.ResourceNotFoundException if no doctor is found with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.customException.ResourceNotFoundException("Doctor not found with id: " + id));
    }

    /**
     * Updates an existing doctor's profile information.
     * Note: The service layer should implement security checks to ensure the authenticated user
     * has permission to update this profile (e.g., is the doctor themselves or an admin).
     *
     * @param id The UUID of the doctor to update.
     * @param updateRequest The DTO containing the updated doctor details.
     * @return ResponseEntity with the updated DoctorDTO and HTTP 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctorProfile(@PathVariable UUID id, @Valid @RequestBody DoctorUpdateRequestDTO updateRequest) {
        DoctorDTO updatedDoctor = doctorService.updateDoctor(id, updateRequest);
        return ResponseEntity.ok(updatedDoctor);
    }

    /**
     * Updates the password for a specific doctor.
     * This dedicated endpoint is a security best practice.
     *
     * @param id The UUID of the doctor whose password is to be updated.
     * @param passwordRequest The DTO containing the current and new password.
     * @return ResponseEntity with a success message and HTTP 200 OK.
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<MessageResponseDTO> updateDoctorPassword(@PathVariable UUID id, @Valid @RequestBody PasswordUpdateRequestDTO passwordRequest) {
        doctorService.updatePassword(id, passwordRequest);
        return ResponseEntity.ok(new MessageResponseDTO("Password updated successfully."));
    }

    /**
     * Deletes a doctor profile.
     * Note: For security, the service layer should verify that the authenticated user
     * has permission to perform this action.
     *
     * @param id The UUID of the doctor to delete.
     * @return ResponseEntity with HTTP 204 No Content upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints ---

    @GetMapping("/search/by-username")
    public ResponseEntity<DoctorDTO> getDoctorByUsername(@RequestParam String username) {
        return doctorService.getDoctorByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.customException.ResourceNotFoundException("Doctor not found with username: " + username));
    }

    @GetMapping("/search/by-email")
    public ResponseEntity<DoctorDTO> getDoctorByEmail(@RequestParam String email) {
        return doctorService.getDoctorByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.customException.ResourceNotFoundException("Doctor not found with email: " + email));
    }

    @GetMapping("/search/by-specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@RequestParam String specialization) {
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/search/by-last-name")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByLastName(@RequestParam String lastName) {
        List<DoctorDTO> doctors = doctorService.getDoctorsByLastName(lastName);
        return ResponseEntity.ok(doctors);
    }
}
