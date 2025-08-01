package com.rohan.Khoj.controller;

import com.rohan.Khoj.dto.AffiliationResponseDTO;
import com.rohan.Khoj.dto.DoctorAffiliationRequestDTO;
import com.rohan.Khoj.dto.DoctorAffiliationUpdateDTO;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.service.AffiliationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor/affiliations")
@RequiredArgsConstructor
public class DoctorAffiliationController {

    private final AffiliationService affiliationService;

    // Doctor initiates a new affiliation request to a clinic.
    @PostMapping("/request")
    public ResponseEntity<AffiliationResponseDTO> createAffiliationRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorAffiliationRequestDTO requestDTO) {

        UUID doctorId = getUserIdFromUserDetails(userDetails); // Assume user details provide the doctor ID

        AffiliationResponseDTO response = affiliationService.createDoctorToClinicRequest(doctorId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // Doctor responds to a clinic's request or updates their own.
    @PutMapping("/update")
    public ResponseEntity<AffiliationResponseDTO> updateAffiliation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DoctorAffiliationUpdateDTO updateDTO) {

        UUID doctorId = getUserIdFromUserDetails(userDetails);

        AffiliationResponseDTO response = affiliationService.processDoctorUpdate(doctorId, updateDTO);
        return ResponseEntity.ok(response);
    }

    // Inside your DoctorAffiliationController or a similar base controller
    private UUID getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof DoctorEntity) {
            return ((DoctorEntity) userDetails).getId();
        }
        // Handle other user types or throw an exception if the user is not of the expected type
        throw new IllegalArgumentException("Invalid user details object for this controller.");
    }
}