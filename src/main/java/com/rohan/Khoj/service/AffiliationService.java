package com.rohan.Khoj.service;

import com.rohan.Khoj.dto.*;
import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import com.rohan.Khoj.entity.*;
import com.rohan.Khoj.repository.ClinicRepository;
import com.rohan.Khoj.repository.DoctorClinicAffiliationRepository;
import com.rohan.Khoj.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AffiliationService {

    private final DoctorClinicAffiliationRepository affiliationRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    @Transactional
    public AffiliationResponseDTO createDoctorToClinicRequest(UUID doctorId, DoctorAffiliationRequestDTO requestDTO) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        ClinicEntity clinic = clinicRepository.findById(requestDTO.getClinicId())
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found"));

        // Use the composite key for existence check and creation
        DoctorClinicAffiliationId affiliationId = new DoctorClinicAffiliationId(doctorId, requestDTO.getClinicId());

        if (affiliationRepository.findById(affiliationId).isPresent()) {
            return new AffiliationResponseDTO(affiliationId, AffiliationStatus.PENDING, "Affiliation request or relationship already exists.", null, null, null, null, null, null, null, null);
        }

        DoctorClinicAffiliationEntity affiliation = DoctorClinicAffiliationEntity.builder()
                .id(affiliationId)
                .doctor(doctor)
                .clinic(clinic)
                .status(AffiliationStatus.PENDING)
                .initiatedBy(AffiliationRequestInitiator.DOCTOR)
                .doctorCharge(requestDTO.getDoctorCharge())
                .shiftDetails(requestDTO.getShiftDetails())
                .joiningDate(requestDTO.getJoiningDate())
                .patientLimits(requestDTO.getPatientLimits())
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        affiliationRepository.save(affiliation);

        return buildResponse(affiliation, "Affiliation request sent to clinic successfully.");
    }

    @Transactional
    public AffiliationResponseDTO createClinicToDoctorRequest(UUID clinicId, ClinicAffiliationRequestDTO requestDTO) {
        ClinicEntity clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found"));
        DoctorEntity doctor = doctorRepository.findById(requestDTO.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        // Use the composite key for existence check and creation
        DoctorClinicAffiliationId affiliationId = new DoctorClinicAffiliationId(requestDTO.getDoctorId(), clinicId);

        if (affiliationRepository.findById(affiliationId).isPresent()) {
            return new AffiliationResponseDTO(affiliationId, AffiliationStatus.PENDING, "Affiliation request or relationship already exists.", null, null, null, null, null, null, null, null);
        }

        DoctorClinicAffiliationEntity affiliation = DoctorClinicAffiliationEntity.builder()
                .id(affiliationId)
                .doctor(doctor)
                .clinic(clinic)
                .status(AffiliationStatus.PENDING)
                .initiatedBy(AffiliationRequestInitiator.CLINIC)
                .clinicCharge(requestDTO.getClinicCharge())
                .shiftDetails(requestDTO.getShiftDetails())
                .joiningDate(requestDTO.getJoiningDate())
                .patientLimits(requestDTO.getPatientLimits())
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        affiliationRepository.save(affiliation);

        return buildResponse(affiliation, "Affiliation request sent to doctor successfully.");
    }

    @Transactional
    public AffiliationResponseDTO processDoctorUpdate(UUID doctorId, DoctorAffiliationUpdateDTO updateDTO) {
        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findById(updateDTO.getAffiliationId())
                .orElseThrow(() -> new IllegalArgumentException("Affiliation not found"));

        if (!affiliation.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("Unauthorized to update this affiliation request.");
        }

        // Action-based logic for doctor
        switch (updateDTO.getStatusAction().toUpperCase()) {
            case "ACCEPT":
                if (affiliation.getInitiatedBy() == AffiliationRequestInitiator.DOCTOR) {
                    return buildResponse(affiliation, "You cannot accept your own request.");
                }
                affiliation.setStatus(AffiliationStatus.APPROVED);
                affiliation.setJoiningDate(updateDTO.getJoiningDate());
                affiliation.setPatientLimits(updateDTO.getPatientLimits());
                affiliation.setShiftDetails(updateDTO.getShiftDetails());
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request approved.");

            case "REJECT":
                affiliation.setStatus(AffiliationStatus.REJECTED);
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request rejected.");

            case "UPDATE":
                if (affiliation.getInitiatedBy() == AffiliationRequestInitiator.DOCTOR) {
                    return buildResponse(affiliation, "You cannot update your own request; wait for a response.");
                }
                affiliation.setDoctorCharge(updateDTO.getDoctorCharge());
                affiliation.setShiftDetails(updateDTO.getShiftDetails());
                affiliation.setJoiningDate(updateDTO.getJoiningDate());
                affiliation.setPatientLimits(updateDTO.getPatientLimits());
                affiliation.setInitiatedBy(AffiliationRequestInitiator.DOCTOR);
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request updated and resent.");

            default:
                throw new IllegalArgumentException("Invalid status action: " + updateDTO.getStatusAction());
        }
    }

    @Transactional
    public AffiliationResponseDTO processClinicUpdate(UUID clinicId, ClinicAffiliationUpdateDTO updateDTO) {
        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findById(updateDTO.getAffiliationId())
                .orElseThrow(() -> new IllegalArgumentException("Affiliation not found"));

        if (!affiliation.getClinic().getId().equals(clinicId)) {
            throw new SecurityException("Unauthorized to update this affiliation request.");
        }

        switch (updateDTO.getStatusAction().toUpperCase()) {
            case "ACCEPT":
                if (affiliation.getInitiatedBy() == AffiliationRequestInitiator.CLINIC) {
                    return buildResponse(affiliation, "You cannot accept your own request.");
                }
                if (updateDTO.getClinicCharge() == null) {
                    throw new IllegalArgumentException("Clinic charge must be provided to accept.");
                }

                affiliation.setStatus(AffiliationStatus.APPROVED);
                affiliation.setClinicCharge(updateDTO.getClinicCharge());
                affiliation.setShiftDetails(updateDTO.getShiftDetails());
                affiliation.setJoiningDate(updateDTO.getJoiningDate());
                affiliation.setPatientLimits(updateDTO.getPatientLimits());
                affiliation.setUpdatedAt(LocalDateTime.now());


                // --- New logic: Persist slot details and patient limits upon acceptance ---
                // If the doctor initiated the request, their proposed data is already in the entity.
                // No extra step is needed here, as the save() call will persist it.

                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request approved.");

            case "REJECT":
                affiliation.setStatus(AffiliationStatus.REJECTED);
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request rejected.");

            case "UPDATE":
                if (affiliation.getInitiatedBy() == AffiliationRequestInitiator.CLINIC) {
                    return buildResponse(affiliation, "You cannot update your own request; wait for a response.");
                }
                affiliation.setClinicCharge(updateDTO.getClinicCharge());
                affiliation.setJoiningDate(updateDTO.getJoiningDate());
                affiliation.setPatientLimits(updateDTO.getPatientLimits());
                affiliation.setShiftDetails(updateDTO.getShiftDetails());
                affiliation.setInitiatedBy(AffiliationRequestInitiator.CLINIC);
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request updated and resent.");

            default:
                throw new IllegalArgumentException("Invalid status action: " + updateDTO.getStatusAction());
        }
    }

    private AffiliationResponseDTO buildResponse(DoctorClinicAffiliationEntity affiliation, String message) {
        return AffiliationResponseDTO.builder()
                .affiliationId(affiliation.getId())
                .status(affiliation.getStatus())
                .message(message)
                .doctorId(affiliation.getDoctor().getId())
                .clinicId(affiliation.getClinic().getId())
                .doctorCharge(affiliation.getDoctorCharge())
                .clinicCharge(affiliation.getClinicCharge())
                .shiftDetails(affiliation.getShiftDetails())
                .patientLimits(affiliation.getPatientLimits())
                .joiningDate(affiliation.getJoiningDate())
                .build();
    }
}