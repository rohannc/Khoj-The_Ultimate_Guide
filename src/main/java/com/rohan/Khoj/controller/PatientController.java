package com.rohan.Khoj.controller;

import com.rohan.Khoj.customException.BadRequestException; // For custom 400
import com.rohan.Khoj.customException.ConflictException;   // For custom 409
import com.rohan.Khoj.customException.ResourceNotFoundException; // For custom 404
import com.rohan.Khoj.dto.PatientDTO; // Response DTO for Patient details
import com.rohan.Khoj.dto.PatientUpdateRequestDTO; // Request DTO for Patient updates
import com.rohan.Khoj.service.PatientService;
import com.rohan.Khoj.service.AppointmentService; // Keep if you're using it for appointment booking endpoints
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // For DTO validation
import java.util.List;
import java.util.UUID; // Using UUID for IDs

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService; // Keep if needed for future appointment endpoints

    // Removed @PostMapping as registration is now handled by RegistrationController:
    // POST /api/auth/register/patient

    /**
     * Retrieves all patients.
     * @return ResponseEntity with a list of PatientDto and HttpStatus.OK, or HttpStatus.NO_CONTENT if no patients found.
     */
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    /**
     * Retrieves a patient by their ID.
     * @param id The UUID of the patient.
     * @return ResponseEntity with PatientDto and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id)); // Use custom exception
    }

    /**
     * Retrieves a patient by their username.
     * @param username The username of the patient.
     * @return ResponseEntity with PatientDto and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<PatientDTO> getPatientByUsername(@PathVariable String username) {
        return patientService.getPatientByUsername(username)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with username: " + username));
    }

    /**
     * Retrieves a patient by their email ID.
     * @param emailId The email ID of the patient.
     * @return ResponseEntity with PatientDto and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/by-email/{emailId}")
    public ResponseEntity<PatientDTO> getPatientByEmail(@PathVariable String emailId) {
        return patientService.getPatientByEmail(emailId)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + emailId));
    }

    /**
     * Retrieves patients by city.
     * @param city The city to search for.
     * @return ResponseEntity with a list of PatientDto and HttpStatus.OK, or HttpStatus.NO_CONTENT if no patients found.
     */
    @GetMapping("/by-city")
    public ResponseEntity<List<PatientDTO>> getPatientsByCity(@RequestParam String city) {
        List<PatientDTO> patients = patientService.getPatientsByCity(city);
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    /**
     * Retrieves patients by blood group.
     * @param bloodGroup The blood group to search for.
     * @return ResponseEntity with a list of PatientDto and HttpStatus.OK, or HttpStatus.NO_CONTENT if no patients found.
     */
    @GetMapping("/by-blood-group")
    public ResponseEntity<List<PatientDTO>> getPatientsByBloodGroup(@RequestParam String bloodGroup) {
        List<PatientDTO> patients = patientService.getPatientsByBloodGroup(bloodGroup);
        if (patients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    /**
     * Updates an existing patient's profile.
     * @param id The UUID of the patient to update.
     * @param updateRequest The DTO containing the updated patient details.
     * @return ResponseEntity with the updated PatientDto and HttpStatus.OK.
     * @throws ResourceNotFoundException if the patient is not found.
     * @throws ConflictException if a uniqueness constraint is violated during update.
     * @throws BadRequestException for other invalid update data.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable UUID id, @Valid @RequestBody PatientUpdateRequestDTO updateRequest) {
        try {
            PatientDTO updatedPatient = patientService.updatePatient(id, updateRequest);
            return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e; // Let @ResponseStatus handle 404
        } catch (ConflictException e) {
            throw e; // Let @ResponseStatus handle 409
        } catch (IllegalArgumentException e) { // Catch potential service-thrown IllegalArgumentException for 400
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error updating patient " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a patient profile.
     * @param id The UUID of the patient to delete.
     * @return ResponseEntity with the PatientDto of the deleted patient and HttpStatus.OK.
     * Or HttpStatus.NOT_FOUND if the patient does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<PatientDTO> deletePatient(@PathVariable UUID id) {
        try {
            PatientDTO deletedPatient = patientService.deletePatient(id);
            // Optionally, return 204 No Content if you don't want to send back the deleted object
            return new ResponseEntity<>(deletedPatient, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e; // Let @ResponseStatus handle 404
        } catch (Exception e) {
            System.err.println("Error deleting patient " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Appointment Endpoints (Uncomment and implement when AppointmentService and DTOs are ready) ---
    // These would typically be managed via request/response DTOs for appointments (e.g., AppointmentRequestDTO, AppointmentDto)
    // and would also likely involve security checks to ensure the patientId matches the authenticated user's ID.

    // @PostMapping("/{patientId}/appointments")
    // public ResponseEntity<AppointmentDto> bookAppointment(@PathVariable UUID patientId, @Valid @RequestBody AppointmentRequestDTO request) {
    //     // Implementation would call appointmentService.scheduleAppointment and map to AppointmentDto
    // }

    // @PutMapping("/{patientId}/appointments/{appointmentId}/reschedule")
    // public ResponseEntity<AppointmentDto> rescheduleAppointment(...) { ... }

    // @PutMapping("/{patientId}/appointments/{appointmentId}/cancel")
    // public ResponseEntity<AppointmentDto> cancelAppointment(...) { ... }

    // @GetMapping("/{patientId}/appointments")
    // public ResponseEntity<List<AppointmentDto>> getPatientAppointments(@PathVariable UUID patientId) { ... }
}