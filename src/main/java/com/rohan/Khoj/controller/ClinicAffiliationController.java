package com.rohan.Khoj.controller;

import com.rohan.Khoj.dto.AffiliationResponseDTO;
import com.rohan.Khoj.dto.ClinicAffiliationRequestDTO;
import com.rohan.Khoj.dto.ClinicAffiliationUpdateDTO;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.service.AffiliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinic/affiliations")
@RequiredArgsConstructor
public class ClinicAffiliationController {

    private final AffiliationService affiliationService;

    // Clinic initiates a new affiliation request to a doctor.
    @PostMapping("/request")
    public ResponseEntity<AffiliationResponseDTO> createAffiliationRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ClinicAffiliationRequestDTO requestDTO) {

        UUID clinicId = getUserIdFromUserDetails(userDetails); // Assume user details provide the clinic ID

        AffiliationResponseDTO response = affiliationService.createClinicToDoctorRequest(clinicId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // Clinic responds to a doctor's request or updates their own.
    @PutMapping("/update")
    public ResponseEntity<AffiliationResponseDTO> updateAffiliation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ClinicAffiliationUpdateDTO updateDTO) {

        UUID clinicId = getUserIdFromUserDetails(userDetails);

        AffiliationResponseDTO response = affiliationService.processClinicUpdate(clinicId, updateDTO);
        return ResponseEntity.ok(response);
    }

    // Inside your DoctorAffiliationController or a similar base controller
    private UUID getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof ClinicEntity) {
            return ((ClinicEntity) userDetails).getId();
        }
        // Handle other user types or throw an exception if the user is not of the expected type
        throw new IllegalArgumentException("Invalid user details object for this controller.");
    }
}