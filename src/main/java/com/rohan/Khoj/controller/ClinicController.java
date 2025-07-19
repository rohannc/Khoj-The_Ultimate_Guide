package com.rohan.Khoj.controller;

import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indicates that this class is a RESTful controller
@RequestMapping("/api/clinics") // Base URL path for all endpoints in this controller
public class ClinicController {

    @Autowired // Injects the ClinicService dependency
    private ClinicService clinicService;

    // POST /api/clinics
    // Creates a new clinic
    @PostMapping
    public ResponseEntity<ClinicEntity> createClinic(@RequestBody ClinicEntity clinic) {
        try {
            ClinicEntity createdClinic = clinicService.createClinic(clinic);
            return new ResponseEntity<>(createdClinic, HttpStatus.CREATED); // Returns 201 Created
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Returns 400 Bad Request if validation fails
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Returns 500 for other errors
        }
    }

    // GET /api/clinics
    // Retrieves all clinics
    @GetMapping
    public ResponseEntity<List<ClinicEntity>> getAllClinics() {
        List<ClinicEntity> clinics = clinicService.getAllClinics();
        if (clinics.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Returns 204 No Content if no clinics found
        }
        return new ResponseEntity<>(clinics, HttpStatus.OK); // Returns 200 OK
    }

    // GET /api/clinics/{id}
    // Retrieves a clinic by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ClinicEntity> getClinicById(@PathVariable Long id) {
        return clinicService.getClinicById(id)
                .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK)) // Returns 200 OK with clinic
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Returns 404 Not Found
    }

    // PUT /api/clinics/{id}
    // Updates an existing clinic
    @PutMapping("/{id}")
    public ResponseEntity<ClinicEntity> updateClinic(@PathVariable Long id, @RequestBody ClinicEntity clinic) {
        try {
            ClinicEntity updatedClinic = clinicService.updateClinic(id, clinic);
            return new ResponseEntity<>(updatedClinic, HttpStatus.OK); // Returns 200 OK with updated clinic
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returns 404 if clinic not found
            }
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Returns 400 for other issues
        }
    }

    // DELETE /api/clinics/{id}
    // Deletes a clinic by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteClinic(@PathVariable Long id) {
        try {
            clinicService.deleteClinic(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Returns 204 No Content on successful deletion
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returns 404 if clinic not found
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Returns 500 for other errors
        }
    }

    // GET /api/clinics/by-name?name={name}
    // Retrieves a clinic by its name
    @GetMapping("/by-name")
    public ResponseEntity<ClinicEntity> getClinicByName(@RequestParam String name) {
        return clinicService.getClinicByName(name)
                .map(clinic -> new ResponseEntity<>(clinic, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET /api/clinics/by-city?city={city}
    // Retrieves clinics by city
    @GetMapping("/by-city")
    public ResponseEntity<List<ClinicEntity>> getClinicsByCity(@RequestParam String city) {
        List<ClinicEntity> clinics = clinicService.getClinicsByCity(city);
        if (clinics.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clinics, HttpStatus.OK);
    }
}