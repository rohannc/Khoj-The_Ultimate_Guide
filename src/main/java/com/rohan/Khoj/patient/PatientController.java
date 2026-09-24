package com.rohan.Khoj.patient;

import com.rohan.Khoj.patient.PatientDTO;
import com.rohan.Khoj.patient.PatientUpdateRequestDTO;
import com.rohan.Khoj.patient.PatientService;
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
 * REST Controller for managing patient-related operations.
 * Relies entirely on the GlobalExceptionHandler for consistent error handling.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Retrieves all patients. Returns an empty list if no patients are found.
     *
     * @return ResponseEntity with a list of PatientDTOs and HTTP 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * Retrieves a specific patient by their unique ID.
     *
     * @param id The UUID of the patient.
     * @return ResponseEntity with the PatientDTO and HTTP 200 OK.
     * @throws com.rohan.Khoj.exception.ResourceNotFoundException if no patient is found with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Patient not found with id: " + id));
    }

    /**
     * Updates an existing patient's profile information.
     * Note: For security, the service layer should verify that the authenticated user
     * has the permission to update this profile (e.g., is the patient themselves or an admin).
     *
     * @param id The UUID of the patient to update.
     * @param updateRequest The DTO containing the updated patient details.
     * @return ResponseEntity with the updated PatientDTO and HTTP 200 OK.
     */
    @PutMapping("/{id}")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<PatientDTO> updatePatientProfile(@PathVariable UUID id, @Valid @RequestBody PatientUpdateRequestDTO updateRequest) {
        PatientDTO updatedPatient = patientService.updatePatient(id, updateRequest);
        return ResponseEntity.ok(updatedPatient);
    }

    /**
     * Updates the password for a specific patient.
     * This is a dedicated endpoint for security best practices.
     * The service layer should handle current password verification and hashing of the new password.
     *
     * @param id The UUID of the patient whose password is to be updated.
     * @param passwordRequest The DTO containing the old and new password.
     * @return ResponseEntity with a success message and HTTP 200 OK.
     */
    @PatchMapping("/{id}/password")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<MessageResponseDTO> updatePatientPassword(@PathVariable UUID id, @Valid @RequestBody PasswordUpdateRequestDTO passwordRequest) {
        patientService.updatePassword(id, passwordRequest);
        return ResponseEntity.ok(new MessageResponseDTO("Password updated successfully."));
    }

    /**
     * Deletes a patient profile.
     * Note: For security, the service layer should verify that the authenticated user
     * has the permission to delete this profile.
     *
     * @param id The UUID of the patient to delete.
     * @return ResponseEntity with HTTP 204 No Content upon successful deletion.
     * @throws com.rohan.Khoj.exception.ResourceNotFoundException if no patient is found with the given ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints ---

    @GetMapping("/search/by-username")
    public ResponseEntity<PatientDTO> getPatientByUsername(@RequestParam String username) {
        return patientService.getPatientByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Patient not found with username: " + username));
    }

    @GetMapping("/search/by-email")
    public ResponseEntity<PatientDTO> getPatientByEmail(@RequestParam String email) {
        return patientService.getPatientByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Patient not found with email: " + email));
    }

    @GetMapping("/search/by-city")
    public ResponseEntity<List<PatientDTO>> getPatientsByCity(@RequestParam String city) {
        List<PatientDTO> patients = patientService.getPatientsByCity(city);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search/by-blood-group")
    public ResponseEntity<List<PatientDTO>> getPatientsByBloodGroup(@RequestParam String bloodGroup) {
        List<PatientDTO> patients = patientService.getPatientsByBloodGroup(bloodGroup);
        return ResponseEntity.ok(patients);
    }
}
