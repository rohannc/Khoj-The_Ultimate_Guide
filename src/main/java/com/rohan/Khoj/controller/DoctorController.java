package com.rohan.Khoj.controller;

import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // POST /api/doctors
    @PostMapping
    public ResponseEntity<DoctorEntity> createDoctor(@RequestBody DoctorEntity doctor) {
        try {
            DoctorEntity createdDoctor = doctorService.createDoctor(doctor);
            return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/doctors
    @GetMapping
    public ResponseEntity<List<DoctorEntity>> getAllDoctors() {
        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    // GET /api/doctors/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DoctorEntity> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(doctor -> new ResponseEntity<>(doctor, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // PUT /api/doctors/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DoctorEntity> updateDoctor(@PathVariable Long id, @RequestBody DoctorEntity doctor) {
        try {
            DoctorEntity updatedDoctor = doctorService.updateDoctor(id, doctor);
            return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/doctors/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/doctors/by-specialization?specialization={specialization}
    @GetMapping("/by-specialization")
    public ResponseEntity<List<DoctorEntity>> getDoctorsBySpecialization(@RequestParam String specialization) {
        List<DoctorEntity> doctors = doctorService.getDoctorsBySpecialization(specialization);
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    // POST /api/doctors/{doctorId}/affiliate/{clinicId}
    // Request body can be used for relationship-specific attributes
    @PostMapping("/{doctorId}/affiliate/{clinicId}")
    public ResponseEntity<DoctorClinicAffiliationEntity> affiliateDoctorToClinic(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId,
            @RequestBody Map<String, String> affiliationDetails) { // Example for additional data
        try {
            LocalDate joiningDate = LocalDate.parse(affiliationDetails.get("joiningDate")); // Assuming "joiningDate" in YYYY-MM-DD
            String role = affiliationDetails.get("roleInClinic");
            String shifts = affiliationDetails.get("shiftDetails");

            DoctorClinicAffiliationEntity affiliation = doctorService.affiliateDoctorToClinic(
                    doctorId, clinicId, joiningDate, role, shifts
            );
            return new ResponseEntity<>(affiliation, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) { // For doctor/clinic not found
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE /api/doctors/{doctorId}/affiliate/{clinicId}
    @DeleteMapping("/{doctorId}/affiliate/{clinicId}")
    public ResponseEntity<HttpStatus> removeDoctorAffiliation(
            @PathVariable Long doctorId,
            @PathVariable Long clinicId) {
        try {
            doctorService.removeDoctorAffiliation(doctorId, clinicId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) { // Covers doctor, clinic, or affiliation not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/doctors/{doctorId}/clinics
    @GetMapping("/{doctorId}/clinics")
    public ResponseEntity<List<ClinicEntity>> getClinicsForDoctor(@PathVariable Long doctorId) {
        try {
            List<ClinicEntity> clinics = doctorService.getClinicsForDoctor(doctorId);
            if (clinics.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinics, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Doctor not found
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}