package com.rohan.Khoj.prescription;

import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.doctor.DoctorRepository;
import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.exception.ForbiddenException;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    /**
     * Fetch all medication items for a patient across all prescriptions.
     * Maps each individual medication item to PrescriptionDTO.
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getPrescriptionsByPatient(UUID patientId) {
        List<PrescriptionItemEntity> items = prescriptionItemRepository.findByPrescriptionPatientId(patientId);
        if (items.isEmpty()) {
            // Fallback for prescriptions without items (if any legacy rows exist)
            return prescriptionRepository.findByPatientId(patientId).stream()
                    .map(p -> modelMapper.map(p, PrescriptionDTO.class))
                    .collect(Collectors.toList());
        }
        return items.stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Fetch all medication items issued by a doctor.
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getPrescriptionsByDoctor(UUID doctorId) {
        List<PrescriptionItemEntity> items = prescriptionItemRepository.findByPrescriptionDoctorId(doctorId);
        if (items.isEmpty()) {
            return prescriptionRepository.findByDoctorIdOrderByIssuedAtDesc(doctorId).stream()
                    .map(p -> modelMapper.map(p, PrescriptionDTO.class))
                    .collect(Collectors.toList());
        }
        return items.stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add an individual medication to a patient.
     * Associates with an existing active prescription for this patient-doctor pair or creates a new one.
     */
    @Transactional
    public PrescriptionDTO addMedication(PrescriptionCreateRequestDTO requestDTO, UserDetails userDetails) {
        DoctorEntity doctor = extractDoctor(userDetails);

        PatientEntity patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + requestDTO.getPatientId()));

        // Find or create active prescription container
        PrescriptionEntity prescription = prescriptionRepository
                .findFirstByPatientIdAndDoctorIdOrderByIssuedAtDesc(patient.getId(), doctor.getId())
                .orElseGet(() -> {
                    PrescriptionEntity newRx = PrescriptionEntity.builder()
                            .patient(patient)
                            .doctor(doctor)
                            .diagnosis("General Consultation")
                            .notes(requestDTO.getInstructions())
                            .isActive(true)
                            .build();
                    return prescriptionRepository.save(newRx);
                });

        PrescriptionItemEntity item = PrescriptionItemEntity.builder()
                .prescription(prescription)
                .medicationName(requestDTO.getMedicationName())
                .dosage(requestDTO.getDosage())
                .frequency(requestDTO.getFrequency())
                .startedAt(requestDTO.getStartedAt() != null ? requestDTO.getStartedAt() : LocalDate.now())
                .durationValue(requestDTO.getDurationValue())
                .durationUnit(requestDTO.getDurationUnit())
                .instructions(requestDTO.getInstructions())
                .isActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true)
                .build();

        PrescriptionItemEntity savedItem = prescriptionItemRepository.save(item);
        return mapItemToDTO(savedItem);
    }

    /**
     * Update an individual medication item (Dosage, Frequency, Duration, Instructions, isActive).
     */
    @Transactional
    public PrescriptionDTO updateMedication(UUID id, PrescriptionUpdateRequestDTO requestDTO, UserDetails userDetails) {
        DoctorEntity doctor = extractDoctor(userDetails);

        PrescriptionItemEntity item = prescriptionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with ID: " + id));

        // Ownership verification: Only the prescribing doctor or clinic doctor can modify
        if (item.getPrescription() != null && item.getPrescription().getDoctor() != null) {
            if (!item.getPrescription().getDoctor().getId().equals(doctor.getId())) {
                throw new ForbiddenException("You are not authorized to update medication prescribed by another doctor.");
            }
        }

        item.setMedicationName(requestDTO.getMedicationName());
        item.setDosage(requestDTO.getDosage());
        item.setFrequency(requestDTO.getFrequency());
        if (requestDTO.getStartedAt() != null) {
            item.setStartedAt(requestDTO.getStartedAt());
        }
        item.setDurationValue(requestDTO.getDurationValue());
        item.setDurationUnit(requestDTO.getDurationUnit());
        item.setInstructions(requestDTO.getInstructions());
        if (requestDTO.getIsActive() != null) {
            item.setIsActive(requestDTO.getIsActive());
        }

        PrescriptionItemEntity updated = prescriptionItemRepository.save(item);
        return mapItemToDTO(updated);
    }

    /**
     * Discontinue medication (soft delete).
     */
    @Transactional
    public PrescriptionDTO discontinueMedication(UUID id, PrescriptionDiscontinueRequestDTO requestDTO, UserDetails userDetails) {
        DoctorEntity doctor = extractDoctor(userDetails);

        PrescriptionItemEntity item = prescriptionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with ID: " + id));

        if (item.getPrescription() != null && item.getPrescription().getDoctor() != null) {
            if (!item.getPrescription().getDoctor().getId().equals(doctor.getId())) {
                throw new ForbiddenException("You are not authorized to discontinue medication prescribed by another doctor.");
            }
        }

        item.setIsActive(false);
        if (requestDTO != null && requestDTO.getReason() != null) {
            item.setDiscontinueReason(requestDTO.getReason());
        }

        PrescriptionItemEntity saved = prescriptionItemRepository.save(item);
        return mapItemToDTO(saved);
    }

    /**
     * Hard delete medication item.
     */
    @Transactional
    public void deleteMedication(UUID id, UserDetails userDetails) {
        DoctorEntity doctor = extractDoctor(userDetails);

        PrescriptionItemEntity item = prescriptionItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medication not found with ID: " + id));

        if (item.getPrescription() != null && item.getPrescription().getDoctor() != null) {
            if (!item.getPrescription().getDoctor().getId().equals(doctor.getId())) {
                throw new ForbiddenException("You are not authorized to delete medication prescribed by another doctor.");
            }
        }

        prescriptionItemRepository.delete(item);
    }

    /**
     * Backward-compatible update for patient logging their medication start date.
     */
    @Transactional
    public PrescriptionDTO updatePrescriptionStartDate(
            UUID id,
            PrescriptionStartDateUpdateRequestDTO requestDTO,
            UserDetails userDetails) {

        // Check whether `id` is a PrescriptionItemEntity ID or a PrescriptionEntity ID
        PrescriptionItemEntity item = prescriptionItemRepository.findById(id).orElse(null);

        if (item != null) {
            PrescriptionEntity prescription = item.getPrescription();
            verifyPatientOwnership(prescription, userDetails);
            item.setStartedAt(requestDTO.getStartedAt());
            PrescriptionItemEntity saved = prescriptionItemRepository.save(item);
            return mapItemToDTO(saved);
        }

        // If not item ID, check PrescriptionEntity ID
        PrescriptionEntity prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));

        verifyPatientOwnership(prescription, userDetails);

        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            throw new BadRequestException("Prescription has no medication items to update.");
        }

        for (PrescriptionItemEntity it : prescription.getItems()) {
            it.setStartedAt(requestDTO.getStartedAt());
        }

        PrescriptionEntity saved = prescriptionRepository.save(prescription);
        return modelMapper.map(saved, PrescriptionDTO.class);
    }

    private void verifyPatientOwnership(PrescriptionEntity prescription, UserDetails userDetails) {
        if (userDetails instanceof PatientEntity patient) {
            if (!prescription.getPatient().getId().equals(patient.getId())) {
                throw new ForbiddenException("You are not authorized to update this prescription.");
            }
        } else {
            throw new ForbiddenException("Only the prescribed patient can update the medication start date.");
        }
    }

    private DoctorEntity extractDoctor(UserDetails userDetails) {
        if (userDetails instanceof DoctorEntity doctor) {
            return doctor;
        }
        // Fallback: look up doctor by username
        if (userDetails != null && userDetails.getUsername() != null) {
            return doctorRepository.findByUsername(userDetails.getUsername())
                    .or(() -> doctorRepository.findByEmailId(userDetails.getUsername()))
                    .orElseThrow(() -> new ForbiddenException("Only authenticated doctors can perform this medication action."));
        }
        throw new ForbiddenException("Authenticated doctor credentials are required.");
    }

    public PrescriptionDTO mapItemToDTO(PrescriptionItemEntity item) {
        PrescriptionEntity prescription = item.getPrescription();
        UUID patientId = null;
        String patientName = null;
        String doctorName = null;
        java.time.LocalDateTime issuedAt = null;

        if (prescription != null) {
            issuedAt = prescription.getIssuedAt();
            if (prescription.getPatient() != null) {
                patientId = prescription.getPatient().getId();
                String fName = prescription.getPatient().getFirstName() != null ? prescription.getPatient().getFirstName() : "";
                String lName = prescription.getPatient().getLastName() != null ? prescription.getPatient().getLastName() : "";
                patientName = (fName + " " + lName).trim();
            }
            if (prescription.getDoctor() != null) {
                String fName = prescription.getDoctor().getFirstName() != null ? prescription.getDoctor().getFirstName() : "";
                String lName = prescription.getDoctor().getLastName() != null ? prescription.getDoctor().getLastName() : "";
                doctorName = (fName + " " + lName).trim();
            }
        }

        LocalDate endDate = null;
        if (item.getStartedAt() != null && item.getDurationValue() != null && item.getDurationUnit() != null) {
            endDate = switch (item.getDurationUnit()) {
                case DAY -> item.getStartedAt().plusDays(item.getDurationValue());
                case WEEK -> item.getStartedAt().plusWeeks(item.getDurationValue());
                case MONTH -> item.getStartedAt().plusMonths(item.getDurationValue());
                case YEAR -> item.getStartedAt().plusYears(item.getDurationValue());
                case ONGOING -> null;
            };
        }

        return PrescriptionDTO.builder()
                .id(item.getId())
                .patientId(patientId)
                .patientName(patientName)
                .doctorName(doctorName)
                .medicationName(item.getMedicationName())
                .dosage(item.getDosage())
                .frequency(item.getFrequency())
                .startedAt(item.getStartedAt())
                .durationValue(item.getDurationValue())
                .durationUnit(item.getDurationUnit())
                .endDate(endDate)
                .instructions(item.getInstructions() != null ? item.getInstructions() : (prescription != null ? prescription.getNotes() : null))
                .isActive(item.getIsActive() != null ? item.getIsActive() : (prescription != null ? prescription.getIsActive() : true))
                .discontinueReason(item.getDiscontinueReason())
                .issuedAt(issuedAt)
                .build();
    }
}
