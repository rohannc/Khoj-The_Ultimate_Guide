package com.rohan.Khoj.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients/{patientId}/dashboard")
@RequiredArgsConstructor
public class PatientDashboardController {

    private final PatientDashboardService dashboardService;

    /**
     * Fetches the aggregated dashboard summary for a specific patient.
     * Includes upcoming appointments, recent vitals, active prescriptions, and unread notifications.
     */
    @GetMapping
    public ResponseEntity<PatientDashboardDTO> getPatientDashboard(@PathVariable UUID patientId) {
        PatientDashboardDTO dashboardData = dashboardService.getDashboardData(patientId);
        return ResponseEntity.ok(dashboardData);
    }
}
