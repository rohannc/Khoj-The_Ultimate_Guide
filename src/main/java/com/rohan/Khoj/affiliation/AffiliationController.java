package com.rohan.Khoj.affiliation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/affiliations")
@RequiredArgsConstructor
public class AffiliationController {

    private final AffiliationService affiliationService;

    @PostMapping("/request")
    public ResponseEntity<AffiliationResponseDTO> createAffiliationRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AffiliationRequestDTO requestDTO) {

        AffiliationResponseDTO response = affiliationService.createAffiliationRequest(userDetails, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<AffiliationResponseDTO> updateAffiliation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AffiliationUpdateDTO updateDTO) {

        AffiliationResponseDTO response = affiliationService.processAffiliationUpdate(userDetails, updateDTO);
        return ResponseEntity.ok(response);
    }
}
