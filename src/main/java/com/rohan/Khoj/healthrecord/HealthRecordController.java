package com.rohan.Khoj.healthrecord;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


import java.util.List;
import java.util.UUID;

@Tag(name = "Health Records", description = "Manage Health Records")
@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @Operation(summary = "Create a record", description = "Creates a new record")
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<HealthRecordDTO> addHealthRecord(
            @PathVariable UUID patientId,
            @Valid @RequestBody HealthRecordDTO dto) {
        return ResponseEntity.ok(healthRecordService.addHealthRecord(patientId, dto));
    }

    @Operation(summary = "Get multiple records", description = "Retrieve a list of records")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<HealthRecordDTO>> getHealthRecords(@PathVariable UUID patientId) {
        return ResponseEntity.ok(healthRecordService.getHealthRecordsForPatient(patientId));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteHealthRecord(@PathVariable UUID recordId) {
        healthRecordService.deleteHealthRecord(recordId);
        return ResponseEntity.noContent().build();
    }
}
