package com.rohan.Khoj.controller;

import com.rohan.Khoj.customException.BadRequestException;
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.*;

import com.rohan.Khoj.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // For DTO validation
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class DoctorController {

    private final DoctorService doctorService; // Injects the DoctorService dependency

    // Removed @PostMapping for createDoctor as registration is handled by RegistrationController:
    // POST /api/auth/register/doctor

    /**
     * Retrieves all doctors.
     * @return ResponseEntity with a list of DoctorDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if no doctors found.
     */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    /**
     * Retrieves a doctor by their ID.
     * @param id The UUID of the doctor.
     * @return ResponseEntity with DoctorDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id)
                .map(doctor -> new ResponseEntity<>(doctor, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    /**
     * Retrieves a doctor by their username.
     * @param username The username of the doctor.
     * @return ResponseEntity with DoctorDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/by-username/{username}")
    public ResponseEntity<DoctorDTO> getDoctorByUsername(@PathVariable String username) {
        return doctorService.getDoctorByUsername(username)
                .map(doctor -> new ResponseEntity<>(doctor, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with username: " + username));
    }

    /**
     * Retrieves a doctor by their email ID.
     * @param emailId The email ID of the doctor.
     * @return ResponseEntity with DoctorDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/by-email/{emailId}")
    public ResponseEntity<DoctorDTO> getDoctorByEmail(@PathVariable String emailId) {
        return doctorService.getDoctorByEmail(emailId)
                .map(doctor -> new ResponseEntity<>(doctor, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email: " + emailId));
    }

    /**
     * Retrieves doctors by specialization.
     * @param specialization The specialization to search for.
     * @return ResponseEntity with a list of DoctorDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if no doctors found.
     */
    @GetMapping("/by-specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@RequestParam String specialization) {
        List<DoctorDTO> doctors = doctorService.getDoctorsBySpecialization(specialization);
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    /**
     * Retrieves doctors by last name.
     * @param lastName The last name to search for.
     * @return ResponseEntity with a list of DoctorDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if no doctors found.
     */
    @GetMapping("/by-last-name")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByLastName(@RequestParam String lastName) {
        List<DoctorDTO> doctors = doctorService.getDoctorsByLastName(lastName);
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    /**
     * Updates an existing doctor's profile.
     * @param id The UUID of the doctor to update.
     * @param updateRequest The DTO containing the updated doctor details.
     * @return ResponseEntity with the updated DoctorDTO and HttpStatus.OK.
     * @throws ResourceNotFoundException if the doctor is not found.
     * @throws ConflictException if a uniqueness constraint is violated during update.
     * @throws BadRequestException for other invalid update data.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable UUID id, @Valid @RequestBody DoctorUpdateRequestDTO updateRequest) {
        try {
            DoctorDTO updatedDoctor = doctorService.updateDoctor(id, updateRequest);
            return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e; // Let @ResponseStatus handle 404/409
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage()); // Catch potential service-thrown IllegalArgumentException for 400
        } catch (Exception e) {
            System.err.println("Error updating doctor " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a doctor profile.
     * @param id The UUID of the doctor to delete.
     * @return ResponseEntity with the DoctorDTO of the deleted doctor and HttpStatus.OK.
     * Or HttpStatus.NOT_FOUND if the doctor does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DoctorDTO> deleteDoctor(@PathVariable UUID id) {
        try {
            DoctorDTO deletedDoctor = doctorService.deleteDoctor(id);
            return new ResponseEntity<>(deletedDoctor, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e; // Let @ResponseStatus handle 404
        } catch (Exception e) {
            System.err.println("Error deleting doctor " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- Doctor-Clinic Affiliation Endpoints ---

    /**
     * Affiliates a doctor with a clinic.
     * @param request The DTO containing doctor ID, clinic ID, and affiliation details.
     * @return ResponseEntity with the created DoctorClinicAffiliationDTO and HttpStatus.CREATED.
     * @throws ResourceNotFoundException if doctor or clinic not found.
     * @throws ConflictException if doctor is already affiliated with the clinic.
     * @throws BadRequestException for other invalid affiliation data.
     */
    @PostMapping("/affiliations")
    public ResponseEntity<DoctorClinicAffiliationDTO> affiliateDoctorToClinic(@Valid @RequestBody DoctorClinicAffiliationRequestDTO request) {
        try {
            DoctorClinicAffiliationDTO affiliation = doctorService.affiliateDoctorToClinic(
                    request.getDoctorId(),
                    request.getClinicId(),
                    request.getJoiningDate(),
                    request.getRoleInClinic(),
                    request.getShiftDetails(),
                    request.getCharge()
            );
            return new ResponseEntity<>(affiliation, HttpStatus.CREATED);
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e; // Let @ResponseStatus handle 404/409
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating doctor-clinic affiliation: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a doctor's affiliation with a clinic.
     * @param doctorId The ID of the doctor.
     * @param clinicId The ID of the clinic.
     * @return ResponseEntity with HttpStatus.NO_CONTENT on successful removal.
     * @throws ResourceNotFoundException if doctor, clinic, or affiliation not found.
     */
    @DeleteMapping("/{doctorId}/affiliations/clinic/{clinicId}")
    public ResponseEntity<Void> removeDoctorAffiliation(
            @PathVariable UUID doctorId,
            @PathVariable UUID clinicId) {
        try {
            doctorService.removeDoctorAffiliation(doctorId, clinicId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } catch (ResourceNotFoundException e) {
            throw e; // Let @ResponseStatus handle 404
        } catch (Exception e) {
            System.err.println("Error removing doctor-clinic affiliation for doctor " + doctorId + " and clinic " + clinicId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all clinics a specific doctor is affiliated with.
     * @param doctorId The ID of the doctor.
     * @return ResponseEntity with a list of ClinicDTOs the doctor works at, or HttpStatus.NO_CONTENT.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    @GetMapping("/{doctorId}/clinics")
    public ResponseEntity<List<ClinicDTO>> getClinicsForDoctor(@PathVariable UUID doctorId) {
        List<ClinicDTO> clinics = doctorService.getClinicsForDoctor(doctorId);
        if (clinics.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clinics, HttpStatus.OK);
    }

    /**
     * Retrieves a specific doctor-clinic affiliation detail.
     * @param affiliationId The ID of the affiliation record.
     * @return ResponseEntity with DoctorClinicAffiliationDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND.
     */
    @GetMapping("/affiliations/{affiliationId}")
    public ResponseEntity<DoctorClinicAffiliationDTO> getDoctorClinicAffiliation(@PathVariable UUID affiliationId) {
        return doctorService.getDoctorClinicAffiliation(affiliationId)
                .map(affiliation -> new ResponseEntity<>(affiliation, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Affiliation not found with id: " + affiliationId));
    }
}