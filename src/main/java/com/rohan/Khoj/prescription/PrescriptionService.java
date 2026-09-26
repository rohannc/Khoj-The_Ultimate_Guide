package com.rohan.Khoj.prescription;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ModelMapper modelMapper;

    public List<PrescriptionDTO> getPrescriptionsByPatient(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(p -> modelMapper.map(p, PrescriptionDTO.class))
                .collect(Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public PrescriptionDTO updatePrescriptionStartDate(
            UUID prescriptionId,
            PrescriptionStartDateUpdateRequestDTO requestDTO,
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        PrescriptionEntity prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new com.rohan.Khoj.exception.ResourceNotFoundException("Prescription not found with ID: " + prescriptionId));

        // Ownership verification: Ensure the authenticated patient owns this prescription
        if (userDetails instanceof com.rohan.Khoj.patient.PatientEntity patient) {
            if (!prescription.getPatient().getId().equals(patient.getId())) {
                throw new com.rohan.Khoj.exception.ForbiddenException("You are not authorized to update this prescription.");
            }
        } else {
            throw new com.rohan.Khoj.exception.ForbiddenException("Only the prescribed patient can update the medication start date.");
        }

        // Validate that prescription items exist
        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            throw new com.rohan.Khoj.exception.BadRequestException("Prescription has no medication items to update.");
        }

        // Update startedAt on all items of this prescription
        for (PrescriptionItemEntity item : prescription.getItems()) {
            item.setStartedAt(requestDTO.getStartedAt());
        }

        PrescriptionEntity saved = prescriptionRepository.save(prescription);
        return modelMapper.map(saved, PrescriptionDTO.class);
    }
}
