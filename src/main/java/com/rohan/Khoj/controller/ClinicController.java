package com.rohan.Khoj.controller;

import com.rohan.Khoj.customException.BadRequestException;
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.ClinicDTO; // Standard casing for DTO name
import com.rohan.Khoj.dto.ClinicUpdateRequestDTO; // Standard casing for DTO name
import com.rohan.Khoj.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class ClinicController {

    private final ClinicService clinicService; // Injects the ClinicService dependency

    // Removed @PostMapping as clinic registration is handled by RegistrationController:
    // POST /api/auth/register/clinic

    /**
     * Retrieves all clinics.
     * @return ResponseEntity with a list of ClinicDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if no clinics found.
     */
    @GetMapping
    public ResponseEntity<List<ClinicDTO>> getAllClinics() {
        List<ClinicDTO> clinics = clinicService.getAllClinics();
        if (clinics.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Returns 204 No Content if no clinics found
        }
        return new ResponseEntity<>(clinics, HttpStatus.OK); // Returns 200 OK
    }

    /**
     * Retrieves a clinic by its ID.
     * @param id The UUID of the clinic.
     * @return ResponseEntity with ClinicDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClinicDTO> getClinicById(@PathVariable UUID id) {
        return clinicService.getClinicById(id)
                .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK)) // Returns 200 OK with clinic
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id)); // Use custom exception
    }

    /**
     * Retrieves a clinic by its name.
     * @param name The name of the clinic.
     * @return ResponseEntity with ClinicDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/by-name")
    public ResponseEntity<ClinicDTO> getClinicByName(@RequestParam String name) {
        return clinicService.getClinicByName(name)
                .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with name: " + name));
    }

    /**
     * Retrieves clinics by city.
     * @param city The city to search for.
     * @return ResponseEntity with a list of ClinicDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if no clinics found.
     */
    @GetMapping("/by-city")
    public ResponseEntity<List<ClinicDTO>> getClinicsByCity(@RequestParam String city) {
        List<ClinicDTO> clinics = clinicService.getClinicsByCity(city);
        if (clinics.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clinics, HttpStatus.OK);
    }

    /**
     * Retrieves clinics by pin code.
     * @param pinCode The pin code to search for.
     * @return ResponseEntity with a list of ClinicDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if no clinics found.
     */
    @GetMapping("/by-pin-code")
    public ResponseEntity<List<ClinicDTO>> getClinicsByPinCode(@RequestParam String pinCode) {
        List<ClinicDTO> clinics = clinicService.getClinicsByPinCode(pinCode);
        if (clinics.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clinics, HttpStatus.OK);
    }

    /**
     * Retrieves a clinic by its email ID.
     * @param emailId The email ID to search for.
     * @return ResponseEntity with ClinicDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/by-email/{emailId}")
    public ResponseEntity<ClinicDTO> getClinicByEmail(@PathVariable String emailId) {
        return clinicService.getClinicByEmail(emailId)
                .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with email: " + emailId));
    }

    /**
     * Updates an existing clinic's profile.
     * @param id The UUID of the clinic to update.
     * @param updateRequest The DTO containing the updated clinic details.
     * @return ResponseEntity with the updated ClinicDTO and HttpStatus.OK.
     * @throws ResourceNotFoundException if the clinic is not found.
     * @throws ConflictException if a uniqueness constraint is violated during update.
     * @throws BadRequestException for other invalid update data.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClinicDTO> updateClinic(@PathVariable UUID id, @Valid @RequestBody ClinicUpdateRequestDTO updateRequest) {
        try {
            ClinicDTO updatedClinic = clinicService.updateClinic(id, updateRequest);
            return new ResponseEntity<>(updatedClinic, HttpStatus.OK);
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e; // Let @ResponseStatus handle 404/409
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error updating clinic " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a clinic profile.
     * @param id The UUID of the clinic to delete.
     * @return ResponseEntity with the ClinicDTO of the deleted clinic and HttpStatus.OK.
     * Or HttpStatus.NOT_FOUND if the clinic does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ClinicDTO> deleteClinic(@PathVariable UUID id) {
        try {
            ClinicDTO deletedClinic = clinicService.deleteClinic(id);
            return new ResponseEntity<>(deletedClinic, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e; // Let @ResponseStatus handle 404
        } catch (Exception e) {
            System.err.println("Error deleting clinic " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}