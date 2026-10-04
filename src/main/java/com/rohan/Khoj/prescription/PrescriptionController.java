package com.rohan.Khoj.prescription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Prescriptions", description = "Manage patient prescriptions and individual medication items")
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Operation(
            summary = "Fetch medicines for a patient",
            description = "Retrieve a list of individual medication items prescribed for the specified patient, including calculated end date and instructions."
    )
    @ApiResponse(responseCode = "200", description = "List of medication items returned successfully")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(@PathVariable UUID patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(patientId));
    }

    @Operation(
            summary = "Get doctor prescriptions",
            description = "Retrieve a list of all medications/prescriptions issued by the doctor."
    )
    @ApiResponse(responseCode = "200", description = "List of doctor prescriptions returned successfully")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDTO>> getDoctorPrescriptions(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctor(doctorId));
    }

    @Operation(
            summary = "Add an individual medication to a patient",
            description = "Allows a doctor to prescribe an individual medication for a patient. Automatically associates with a prescription container."
    )
    @ApiResponse(responseCode = "201", description = "Medication created and added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failure")
    @ApiResponse(responseCode = "403", description = "Forbidden - caller is not an authorized doctor")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @PostMapping
    public ResponseEntity<PrescriptionDTO> addMedication(
            @Valid @RequestBody PrescriptionCreateRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PrescriptionDTO created = prescriptionService.addMedication(requestDTO, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Add an individual medication to a patient (alternative path)",
            description = "Alternative endpoint to prescribe a medication directly by patient ID."
    )
    @ApiResponse(responseCode = "201", description = "Medication created successfully")
    @PostMapping("/patient/{patientId}/medications")
    public ResponseEntity<PrescriptionDTO> addMedicationForPatient(
            @PathVariable UUID patientId,
            @Valid @RequestBody PrescriptionCreateRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        requestDTO.setPatientId(patientId);
        PrescriptionDTO created = prescriptionService.addMedication(requestDTO, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Update an individual medication",
            description = "Allows a doctor to update dosage, frequency, duration, start date, instructions, or active status of a medication."
    )
    @ApiResponse(responseCode = "200", description = "Medication updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "403", description = "Forbidden - doctor did not prescribe this medication")
    @ApiResponse(responseCode = "404", description = "Medication not found")
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> updateMedication(
            @PathVariable UUID id,
            @Valid @RequestBody PrescriptionUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PrescriptionDTO updated = prescriptionService.updateMedication(id, requestDTO, userDetails);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Update an individual medication (alternative path)",
            description = "Alternative endpoint to update medication details by medication item ID."
    )
    @ApiResponse(responseCode = "200", description = "Medication updated successfully")
    @PutMapping("/medications/{id}")
    public ResponseEntity<PrescriptionDTO> updateMedicationItem(
            @PathVariable UUID id,
            @Valid @RequestBody PrescriptionUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PrescriptionDTO updated = prescriptionService.updateMedication(id, requestDTO, userDetails);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Discontinue an individual medication",
            description = "Soft deletes/discontinues a medication by setting isActive to false and recording the discontinuation reason."
    )
    @ApiResponse(responseCode = "200", description = "Medication discontinued successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - unauthorized to modify this medication")
    @ApiResponse(responseCode = "404", description = "Medication not found")
    @PatchMapping("/{id}/discontinue")
    public ResponseEntity<PrescriptionDTO> discontinueMedication(
            @PathVariable UUID id,
            @RequestBody(required = false) PrescriptionDiscontinueRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PrescriptionDTO discontinued = prescriptionService.discontinueMedication(id, requestDTO, userDetails);
        return ResponseEntity.ok(discontinued);
    }

    @Operation(
            summary = "Delete an individual medication",
            description = "Permanently removes a medication item from the prescription."
    )
    @ApiResponse(responseCode = "204", description = "Medication deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - doctor did not prescribe this medication")
    @ApiResponse(responseCode = "404", description = "Medication not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedication(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        prescriptionService.deleteMedication(id, userDetails);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update medication start date",
            description = "Allows the authenticated patient to log or update the date they started taking the prescribed medication. Automatically recalculates adherence and end date."
    )
    @ApiResponse(responseCode = "200", description = "Start date updated successfully and end date recomputed")
    @ApiResponse(responseCode = "400", description = "Invalid date or missing parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - patient is not the owner of this prescription")
    @ApiResponse(responseCode = "404", description = "Prescription or medication not found")
    @PatchMapping("/{id}/start-date")
    public ResponseEntity<PrescriptionDTO> updateStartDate(
            @PathVariable UUID id,
            @Valid @RequestBody PrescriptionStartDateUpdateRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        PrescriptionDTO updated = prescriptionService.updatePrescriptionStartDate(id, requestDTO, userDetails);
        return ResponseEntity.ok(updated);
    }
}
