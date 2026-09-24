package com.rohan.Khoj.affiliation;

import com.rohan.Khoj.clinic.ClinicRepository;
import com.rohan.Khoj.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.doctor.DoctorEntity;

@Service
@RequiredArgsConstructor
public class AffiliationService {

    private final DoctorClinicAffiliationRepository affiliationRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    @Transactional
    public AffiliationResponseDTO createAffiliationRequest(UserDetails userDetails, AffiliationRequestDTO requestDTO) {
        DoctorEntity doctor;
        ClinicEntity clinic;
        AffiliationRequestInitiator initiator;
        UUID doctorId, clinicId;
        Double doctorCharge = null;
        Double clinicCharge = null;

        if (userDetails instanceof DoctorEntity) {
            doctor = (DoctorEntity) userDetails;
            doctorId = doctor.getId();
            clinicId = requestDTO.getTargetId();
            clinic = clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new IllegalArgumentException("Clinic not found"));
            initiator = AffiliationRequestInitiator.DOCTOR;
            doctorCharge = requestDTO.getCharge();
        } else if (userDetails instanceof ClinicEntity) {
            clinic = (ClinicEntity) userDetails;
            clinicId = clinic.getId();
            doctorId = requestDTO.getTargetId();
            doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
            initiator = AffiliationRequestInitiator.CLINIC;
            clinicCharge = requestDTO.getCharge();
        } else {
            throw new IllegalArgumentException("User must be either a Doctor or a Clinic to create an affiliation request.");
        }

        if (affiliationRepository.findByDoctorAndClinic(doctor, clinic).isPresent()) {
            return AffiliationResponseDTO.builder()
                    .status(AffiliationStatus.PENDING)
                    .message("Affiliation request or relationship already exists.")
                    .build();
        }

        DoctorClinicAffiliationEntity affiliation = DoctorClinicAffiliationEntity.builder()
                .doctor(doctor)
                .clinic(clinic)
                .status(AffiliationStatus.PENDING)
                .initiatedBy(initiator)
                .doctorCharge(doctorCharge)
                .clinicCharge(clinicCharge)
                .joiningDate(requestDTO.getJoiningDate())
                .dailyPatientLimit(requestDTO.getPatientLimits())
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        affiliationRepository.save(affiliation);

        return buildResponse(affiliation, "Affiliation request sent successfully.");
    }

    @Transactional
    public AffiliationResponseDTO processAffiliationUpdate(UserDetails userDetails, AffiliationUpdateDTO updateDTO) {
        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findById(updateDTO.getAffiliationId())
                .orElseThrow(() -> new IllegalArgumentException("Affiliation not found"));

        AffiliationRequestInitiator callerRole;

        if (userDetails instanceof DoctorEntity) {
            if (!affiliation.getDoctor().getId().equals(((DoctorEntity) userDetails).getId())) {
                throw new SecurityException("Unauthorized to update this affiliation request.");
            }
            callerRole = AffiliationRequestInitiator.DOCTOR;
        } else if (userDetails instanceof ClinicEntity) {
            if (!affiliation.getClinic().getId().equals(((ClinicEntity) userDetails).getId())) {
                throw new SecurityException("Unauthorized to update this affiliation request.");
            }
            callerRole = AffiliationRequestInitiator.CLINIC;
        } else {
            throw new IllegalArgumentException("User must be either a Doctor or a Clinic to process an update.");
        }

        switch (updateDTO.getStatusAction().toUpperCase()) {
            case "ACCEPT":
                if (affiliation.getInitiatedBy() == callerRole) {
                    return buildResponse(affiliation, "You cannot accept your own request.");
                }
                
                // When accepting, if you are the responder, you might need to supply a charge.
                if (callerRole == AffiliationRequestInitiator.CLINIC && updateDTO.getCharge() == null) {
                     // For clinics accepting a doctor's request, they may need to define their charge (or inherit if not strict)
                     // Keeping with previous logic: Clinic must provide clinicCharge to accept.
                     throw new IllegalArgumentException("Charge must be provided to accept.");
                }

                affiliation.setStatus(AffiliationStatus.APPROVED);
                
                // Only update these fields if provided during ACCEPT
                if (updateDTO.getCharge() != null) {
                    if (callerRole == AffiliationRequestInitiator.CLINIC) {
                        affiliation.setClinicCharge(updateDTO.getCharge());
                    } else {
                        affiliation.setDoctorCharge(updateDTO.getCharge());
                    }
                }
                if (updateDTO.getJoiningDate() != null) affiliation.setJoiningDate(updateDTO.getJoiningDate());
                if (updateDTO.getPatientLimits() != null) affiliation.setDailyPatientLimit(updateDTO.getPatientLimits());
                
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request approved.");

            case "REJECT":
                affiliation.setStatus(AffiliationStatus.REJECTED);
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request rejected.");

            case "UPDATE":
                if (affiliation.getInitiatedBy() == callerRole) {
                    return buildResponse(affiliation, "You cannot update your own request; wait for a response.");
                }
                if (callerRole == AffiliationRequestInitiator.DOCTOR) {
                    affiliation.setDoctorCharge(updateDTO.getCharge());
                } else {
                    affiliation.setClinicCharge(updateDTO.getCharge());
                }
                
                affiliation.setJoiningDate(updateDTO.getJoiningDate());
                affiliation.setDailyPatientLimit(updateDTO.getPatientLimits());
                affiliation.setInitiatedBy(callerRole); // Flip initiator
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
                .patientLimits(affiliation.getDailyPatientLimit())
                .joiningDate(affiliation.getJoiningDate())
                .build();
    }
}