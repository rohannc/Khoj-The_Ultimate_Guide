package com.rohan.Khoj.healthrecord;

import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public HealthRecordDTO addHealthRecord(UUID patientId, HealthRecordDTO dto) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        HealthRecordEntity entity = HealthRecordEntity.builder()
                .patient(patient)
                .documentTitle(dto.getDocumentTitle())
                .documentType(dto.getDocumentType())
                .documentUrl(dto.getDocumentUrl())
                .testDate(dto.getTestDate())
                .build();

        HealthRecordEntity saved = healthRecordRepository.save(entity);
        return mapToDTO(saved);
    }

    public List<HealthRecordDTO> getHealthRecordsForPatient(UUID patientId) {
        return healthRecordRepository.findAll().stream()
                .filter(record -> record.getPatient().getId().equals(patientId))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteHealthRecord(UUID recordId) {
        healthRecordRepository.deleteById(recordId);
    }

    private HealthRecordDTO mapToDTO(HealthRecordEntity entity) {
        HealthRecordDTO dto = modelMapper.map(entity, HealthRecordDTO.class);
        dto.setPatientId(entity.getPatient().getId());
        return dto;
    }
}
