package com.rohan.Khoj.clinic;

import com.rohan.Khoj.clinic.ClinicDTO;
import com.rohan.Khoj.clinic.ClinicUpdateRequestDTO;
import com.rohan.Khoj.clinic.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import com.rohan.Khoj.exception.GlobalExceptionHandler;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.common.MessageResponseDTO;
import com.rohan.Khoj.common.PasswordUpdateRequestDTO;

/**
 * REST Controller for managing clinic-related operations.
 * Relies on the GlobalExceptionHandler for consistent, centralized error handling.
 */
@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;

    /**
     * Retrieves all clinics. Returns an empty list if none are found.
     *
     * @return ResponseEntity with a list of ClinicDTOs and HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<ClinicDTO>> getAllClinics() {
        List<ClinicDTO> clinics = clinicService.getAllClinics();
        return ResponseEntity.ok(clinics);
    }

    /**
     * Retrieves a specific clinic by its unique ID.
     *
     * @param id The UUID of the clinic.
     * @return ResponseEntity with the ClinicDTO and HTTP 200 OK.
     * @throws com.rohan.Khoj.exception.ResourceNotFoundException if no clinic is found with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClinicDTO> getClinicById(@PathVariable UUID id) {
        return clinicService.getClinicById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Clinic not found with id: " + id));
    }

    /**
     * Updates an existing clinic's profile information.
     * The service layer should ensure the authenticated user has permission to perform this update.
     *
     * @param id The UUID of the clinic to update.
     * @param updateRequest The DTO containing the updated clinic details.
     * @return ResponseEntity with the updated ClinicDTO and HTTP 200 OK.
     */
    @PutMapping("/{id}")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<ClinicDTO> updateClinicProfile(@PathVariable UUID id, @Valid @RequestBody ClinicUpdateRequestDTO updateRequest) {
        ClinicDTO updatedClinic = clinicService.updateClinic(id, updateRequest);
        return ResponseEntity.ok(updatedClinic);
    }

    /**
     * Updates the password for a specific clinic.
     *
     * @param id The UUID of the clinic.
     * @param passwordRequest The DTO with current and new password.
     * @return ResponseEntity with a success message and HTTP 200 OK.
     */
    @PatchMapping("/{id}/password")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<MessageResponseDTO> updateClinicPassword(@PathVariable UUID id, @Valid @RequestBody PasswordUpdateRequestDTO passwordRequest) {
        clinicService.updatePassword(id, passwordRequest);
        return ResponseEntity.ok(new MessageResponseDTO("Password updated successfully."));
    }

    /**
     * Deletes a clinic profile.
     *
     * @param id The UUID of the clinic to delete.
     * @return ResponseEntity with HTTP 204 No Content upon successful deletion.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<Void> deleteClinic(@PathVariable UUID id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints ---

    @GetMapping("/search/by-name")
    public ResponseEntity<ClinicDTO> getClinicByName(@RequestParam String name) {
        return clinicService.getClinicByName(name)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Clinic not found with name: " + name));
    }

    @GetMapping("/search/by-email")
    public ResponseEntity<ClinicDTO> getClinicByEmail(@RequestParam String email) {
        return clinicService.getClinicByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Clinic not found with email: " + email));
    }

    @GetMapping("/search/by-city")
    public ResponseEntity<List<ClinicDTO>> getClinicsByCity(@RequestParam String city) {
        List<ClinicDTO> clinics = clinicService.getClinicsByCity(city);
        return ResponseEntity.ok(clinics);
    }

    @GetMapping("/search/by-pin-code")
    public ResponseEntity<List<ClinicDTO>> getClinicsByPinCode(@RequestParam String pinCode) {
        List<ClinicDTO> clinics = clinicService.getClinicsByPinCode(pinCode);
        return ResponseEntity.ok(clinics);
    }
}
