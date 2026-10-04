package com.rohan.Khoj.affiliation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Affiliations", description = "Manage Doctor and Clinic affiliations, proposals, counter-negotiations, and 7-day shift scheduling")
@RestController
@RequestMapping("/api/affiliations")
@RequiredArgsConstructor
public class AffiliationController {

    private final AffiliationService affiliationService;

    @Operation(
            summary = "Create an affiliation request",
            description = "Initiates a new affiliation request from either a Doctor or a Clinic. The payload includes proposed consultation charges, daily patient limit, joining date, and 7-day shift schedule."
    )
    @ApiResponse(responseCode = "200", description = "Affiliation proposal created or existing relationship status returned")
    @ApiResponse(responseCode = "400", description = "Invalid payload or validation failure")
    @ApiResponse(responseCode = "403", description = "Forbidden - user is neither doctor nor clinic")
    @PostMapping("/request")
    public ResponseEntity<AffiliationResponseDTO> createAffiliationRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AffiliationRequestDTO requestDTO) {

        AffiliationResponseDTO response = affiliationService.createAffiliationRequest(userDetails, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update, accept, reject, or counter-negotiate an affiliation request",
            description = "Responds to an affiliation proposal. Use action 'ACCEPT' to approve the affiliation, 'REJECT' to decline, or 'UPDATE' to submit a counter-offer with revised charges, patient limits, or 7-day shift details."
    )
    @ApiResponse(responseCode = "200", description = "Affiliation status updated successfully with updated 7-day schedule")
    @ApiResponse(responseCode = "400", description = "Invalid action, missing parameters, or self-approval attempt")
    @ApiResponse(responseCode = "403", description = "Forbidden - unauthorized party")
    @PutMapping("/update")
    public ResponseEntity<AffiliationResponseDTO> updateAffiliation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AffiliationUpdateDTO updateDTO) {

        AffiliationResponseDTO response = affiliationService.processAffiliationUpdate(userDetails, updateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get affiliations for a doctor",
            description = "Retrieves all clinic affiliations for the given doctor ID. Each affiliation entry includes clinic details, fee breakdown, and the complete 7-day shift schedule ('MONDAY' through 'SUNDAY')."
    )
    @ApiResponse(responseCode = "200", description = "List of doctor affiliations with 7-day shifts returned")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AffiliationResponseDTO>> getDoctorAffiliations(
            @Parameter(description = "UUID of the doctor", required = true)
            @PathVariable UUID doctorId,
            @Parameter(description = "Filter by affiliation status: PENDING, APPROVED, REJECTED, TERMINATED")
            @RequestParam(required = false) AffiliationStatus status) {
        return ResponseEntity.ok(affiliationService.getAffiliationsForDoctor(doctorId, status));
    }

    @Operation(
            summary = "Get affiliations for a clinic",
            description = "Retrieves all doctor affiliations for the given clinic ID. Each affiliation entry includes doctor details, fee breakdown, and the complete 7-day shift schedule ('MONDAY' through 'SUNDAY')."
    )
    @ApiResponse(responseCode = "200", description = "List of clinic affiliations with 7-day shifts returned")
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<AffiliationResponseDTO>> getClinicAffiliations(
            @Parameter(description = "UUID of the clinic", required = true)
            @PathVariable UUID clinicId,
            @Parameter(description = "Filter by affiliation status: PENDING, APPROVED, REJECTED, TERMINATED")
            @RequestParam(required = false) AffiliationStatus status) {
        return ResponseEntity.ok(affiliationService.getAffiliationsForClinic(clinicId, status));
    }
}
