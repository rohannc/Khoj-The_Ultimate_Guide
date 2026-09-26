package com.rohan.Khoj.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Patient Dashboard", description = "Endpoints for retrieving aggregated dashboard data for patients")
@RestController
@RequestMapping("/api/patients/{patientId}/dashboard")
@RequiredArgsConstructor
public class PatientDashboardController {

    private final PatientDashboardService dashboardService;

    /**
     * Fetches the aggregated dashboard summary for a specific patient.
     * Includes patient personal details, immediate upcoming appointment, recent vitals,
     * two active prescriptions, two recent health records, and unread notifications.
     */
    @Operation(
            summary = "Get aggregated patient dashboard",
            description = "Retrieves patient demographics, the immediate next appointment, all recent vitals, two active prescriptions, two recent health records, and top unread notifications."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved patient dashboard data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDashboardDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found with the specified ID"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Valid JWT token required"
            )
    })
    @GetMapping
    public ResponseEntity<PatientDashboardDTO> getPatientDashboard(
            @Parameter(description = "UUID of the patient", required = true, example = "0b1cd526-508f-4b5d-8540-6991925669a1")
            @PathVariable UUID patientId) {
        PatientDashboardDTO dashboardData = dashboardService.getDashboardData(patientId);
        return ResponseEntity.ok(dashboardData);
    }
}
