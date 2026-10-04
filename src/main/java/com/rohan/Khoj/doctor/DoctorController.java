package com.rohan.Khoj.doctor;

import com.rohan.Khoj.doctor.DoctorDTO;
import com.rohan.Khoj.doctor.DoctorUpdateRequestDTO;
import com.rohan.Khoj.doctor.DoctorService;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for managing doctor-related operations.
 * This controller delegates all exception handling to the GlobalExceptionHandler for consistency.
 */
@Tag(name = "Doctors", description = "Operations related to doctor profiles and searches")
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
    @Operation(summary = "Get all doctors", description = "Retrieves a list of all registered doctors")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor list")
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
     * @throws com.rohan.Khoj.exception.ResourceNotFoundException if no doctor is found with the given ID.
     */
    @Operation(summary = "Get doctor by ID", description = "Retrieves a doctor's profile by their unique ID, including primary and secondary phone numbers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor profile"),
        @ApiResponse(responseCode = "404", description = "Doctor not found with the specified ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Doctor not found with id: " + id));
    }

    /**
     * Retrieves aggregated dashboard data for a doctor (stats, today's appointments, upcoming, active/pending affiliations, recent patients, prescriptions).
     *
     * @param id The UUID of the doctor.
     * @return ResponseEntity with the DoctorDashboardDTO and HTTP 200 OK.
     */
    @Operation(summary = "Get doctor dashboard", description = "Retrieves aggregated statistics and activity for doctor dashboard")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor dashboard")
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<DoctorDashboardDTO> getDoctorDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorDashboard(id));
    }

    /**
     * Retrieves all unique patients who have booked or attended appointments with this doctor.
     *
     * @param id The UUID of the doctor.
     * @return ResponseEntity with list of PatientDTO and HTTP 200 OK.
     */
    @Operation(summary = "Get doctor's patients", description = "Retrieves unique patients who have appointments with this doctor")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved patients list")
    @GetMapping("/{id}/patients")
    public ResponseEntity<List<com.rohan.Khoj.patient.PatientDTO>> getDoctorPatients(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorPatients(id));
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
    @Operation(summary = "Update doctor profile", description = "Updates doctor profile details including primary and secondary mobile numbers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated doctor profile"),
        @ApiResponse(responseCode = "400", description = "Validation failure or invalid data"),
        @ApiResponse(responseCode = "404", description = "Doctor not found"),
        @ApiResponse(responseCode = "409", description = "Username or email already in use")
    })
    @PutMapping("/{id}")
    @PreAuthorize("principal.id.toString() == #id.toString()")
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
    @Operation(summary = "Update doctor password", description = "Updates doctor's account password")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @PatchMapping("/{id}/password")
    @PreAuthorize("principal.id.toString() == #id.toString()")
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
    @Operation(summary = "Delete doctor", description = "Deletes a doctor's account and profile")
    @ApiResponse(responseCode = "204", description = "Doctor deleted successfully")
    @DeleteMapping("/{id}")
    @PreAuthorize("principal.id.toString() == #id.toString()")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints ---

    @Operation(summary = "Get doctor by username", description = "Finds a single doctor by username")
    @GetMapping("/search/by-username")
    public ResponseEntity<DoctorDTO> getDoctorByUsername(@RequestParam String username) {
        return doctorService.getDoctorByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Doctor not found with username: " + username));
    }

    @Operation(summary = "Get doctor by email", description = "Finds a single doctor by email address")
    @GetMapping("/search/by-email")
    public ResponseEntity<DoctorDTO> getDoctorByEmail(@RequestParam String email) {
        return doctorService.getDoctorByEmail(email)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Doctor not found with email: " + email));
    }

    @Operation(summary = "Search doctors by specialization", description = "Finds doctors matching a specialization query")
    @GetMapping("/search/by-specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@RequestParam String specialization) {
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Search doctors by last name", description = "Finds doctors matching a last name query")
    @GetMapping("/search/by-last-name")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByLastName(@RequestParam String lastName) {
        List<DoctorDTO> doctors = doctorService.getDoctorsByLastName(lastName);
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Search doctors by qualification", description = "Finds doctors matching a qualification query")
    @GetMapping("/search/by-qualification")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByQualification(@RequestParam String qualification) {
        List<DoctorDTO> doctors = doctorService.getDoctorsByQualification(qualification);
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Search doctors with pagination and filters", description = "Searches doctors by query, specialization, city, and gender")
    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<DoctorDTO>> searchDoctors(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) com.rohan.Khoj.common.Gender gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.searchDoctors(query, specialization, city, gender, pageable));
    }
}
