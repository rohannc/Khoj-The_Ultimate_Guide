package com.rohan.Khoj.prescription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import java.util.List;
import java.util.UUID;

@Tag(name = "Prescriptions", description = "Manage Prescriptions")
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Operation(summary = "Get multiple records", description = "Retrieve a list of records")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(@PathVariable UUID patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatient(patientId));
    }

    @Operation(
            summary = "Update medication start date",
            description = "Allows the authenticated patient to log or update the date they started taking the prescribed medication. Automatically recalculates adherence and end date."
    )
    @ApiResponse(responseCode = "200", description = "Start date updated successfully and end date recomputed")
    @ApiResponse(responseCode = "400", description = "Invalid date or missing parameters")
    @ApiResponse(responseCode = "403", description = "Forbidden - patient is not the owner of this prescription")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    @PatchMapping("/{id}/start-date")
    public ResponseEntity<PrescriptionDTO> updateStartDate(
            @PathVariable UUID id,
            @Valid @RequestBody PrescriptionStartDateUpdateRequestDTO requestDTO,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        PrescriptionDTO updated = prescriptionService.updatePrescriptionStartDate(id, requestDTO, userDetails);
        return ResponseEntity.ok(updated);
    }
}
