package com.rohan.Khoj.healthrecord;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/health-records")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @PostMapping("/patient/{patientId}")
    public ResponseEntity<HealthRecordDTO> addHealthRecord(
            @PathVariable UUID patientId,
            @RequestBody HealthRecordDTO dto) {
        return ResponseEntity.ok(healthRecordService.addHealthRecord(patientId, dto));
    }

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
