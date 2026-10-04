package com.rohan.Khoj.affiliation;

import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.clinic.ClinicRepository;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.doctor.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AffiliationService {

    private final DoctorClinicAffiliationRepository affiliationRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public AffiliationResponseDTO createAffiliationRequest(UserDetails userDetails, AffiliationRequestDTO requestDTO) {
        DoctorEntity doctor;
        ClinicEntity clinic;
        AffiliationRequestInitiator initiator;
        AffiliationActionRequiredBy actionRequiredBy;
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
            actionRequiredBy = AffiliationActionRequiredBy.CLINIC;
            doctorCharge = requestDTO.getCharge();
        } else if (userDetails instanceof ClinicEntity) {
            clinic = (ClinicEntity) userDetails;
            clinicId = clinic.getId();
            doctorId = requestDTO.getTargetId();
            doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
            initiator = AffiliationRequestInitiator.CLINIC;
            actionRequiredBy = AffiliationActionRequiredBy.DOCTOR;
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
                .actionRequiredBy(actionRequiredBy)
                .doctorCharge(doctorCharge)
                .clinicCharge(clinicCharge)
                .joiningDate(requestDTO.getJoiningDate())
                .dailyPatientLimit(requestDTO.getPatientLimits())
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applyShiftDetails(affiliation, requestDTO.getShiftDetails());
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

                if (callerRole == AffiliationRequestInitiator.CLINIC && updateDTO.getCharge() == null && affiliation.getClinicCharge() == null) {
                    throw new IllegalArgumentException("Clinic charge must be provided to accept.");
                } else if (callerRole == AffiliationRequestInitiator.DOCTOR && updateDTO.getCharge() == null && affiliation.getDoctorCharge() == null) {
                    throw new IllegalArgumentException("Doctor charge must be provided to accept.");
                }

                affiliation.setStatus(AffiliationStatus.APPROVED);

                if (updateDTO.getCharge() != null) {
                    if (callerRole == AffiliationRequestInitiator.CLINIC) {
                        affiliation.setClinicCharge(updateDTO.getCharge());
                    } else {
                        affiliation.setDoctorCharge(updateDTO.getCharge());
                    }
                }
                if (updateDTO.getJoiningDate() != null) affiliation.setJoiningDate(updateDTO.getJoiningDate());
                if (updateDTO.getPatientLimits() != null) affiliation.setDailyPatientLimit(updateDTO.getPatientLimits());
                if (updateDTO.getShiftDetails() != null) applyShiftDetails(affiliation, updateDTO.getShiftDetails());

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
                    affiliation.setActionRequiredBy(AffiliationActionRequiredBy.CLINIC);
                } else {
                    affiliation.setClinicCharge(updateDTO.getCharge());
                    affiliation.setActionRequiredBy(AffiliationActionRequiredBy.DOCTOR);
                }

                if (updateDTO.getJoiningDate() != null) affiliation.setJoiningDate(updateDTO.getJoiningDate());
                if (updateDTO.getPatientLimits() != null) affiliation.setDailyPatientLimit(updateDTO.getPatientLimits());
                if (updateDTO.getShiftDetails() != null) applyShiftDetails(affiliation, updateDTO.getShiftDetails());

                affiliation.setInitiatedBy(callerRole); // Flip initiator
                affiliation.setUpdatedAt(LocalDateTime.now());
                affiliationRepository.save(affiliation);
                return buildResponse(affiliation, "Affiliation request updated and resent.");

            default:
                throw new IllegalArgumentException("Invalid status action: " + updateDTO.getStatusAction());
        }
    }

    public List<AffiliationResponseDTO> getAffiliationsForDoctor(UUID doctorId, AffiliationStatus status) {
        return affiliationRepository.findByDoctorIdAndStatus(doctorId, status).stream()
                .map(aff -> buildResponse(aff, null))
                .collect(Collectors.toList());
    }

    public List<AffiliationResponseDTO> getAffiliationsForClinic(UUID clinicId, AffiliationStatus status) {
        return affiliationRepository.findByClinicIdAndStatus(clinicId, status).stream()
                .map(aff -> buildResponse(aff, null))
                .collect(Collectors.toList());
    }

    /**
     * Parses the given shiftDetails map and applies the start and end times to the entity.
     * Supports formats: "HH:mm - HH:mm", "HH:mm-HH:mm", or "OFF"/"Closed".
     */
    public void applyShiftDetails(DoctorClinicAffiliationEntity entity, Map<String, String> shiftDetails) {
        if (shiftDetails == null) {
            return;
        }

        for (Map.Entry<String, String> entry : shiftDetails.entrySet()) {
            String day = entry.getKey().trim().toUpperCase();
            String timeRange = entry.getValue();

            LocalTime start = null;
            LocalTime end = null;

            if (timeRange != null && !timeRange.isBlank()) {
                String clean = timeRange.trim().toUpperCase();
                if (!clean.equals("OFF") && !clean.equals("CLOSED") && !clean.equals("NONE") && clean.contains("-")) {
                    String[] parts = clean.split("-");
                    if (parts.length == 2) {
                        try {
                            start = LocalTime.parse(parts[0].trim(), DateTimeFormatter.ofPattern("H:mm"));
                            end = LocalTime.parse(parts[1].trim(), DateTimeFormatter.ofPattern("H:mm"));
                        } catch (Exception e) {
                            // Fallback to strict HH:mm
                            try {
                                start = LocalTime.parse(parts[0].trim());
                                end = LocalTime.parse(parts[1].trim());
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }

            switch (day) {
                case "MONDAY" -> { entity.setMondayStart(start); entity.setMondayEnd(end); }
                case "TUESDAY" -> { entity.setTuesdayStart(start); entity.setTuesdayEnd(end); }
                case "WEDNESDAY" -> { entity.setWednesdayStart(start); entity.setWednesdayEnd(end); }
                case "THURSDAY" -> { entity.setThursdayStart(start); entity.setThursdayEnd(end); }
                case "FRIDAY" -> { entity.setFridayStart(start); entity.setFridayEnd(end); }
                case "SATURDAY" -> { entity.setSaturdayStart(start); entity.setSaturdayEnd(end); }
                case "SUNDAY" -> { entity.setSundayStart(start); entity.setSundayEnd(end); }
            }
        }
    }

    /**
     * Builds complete 7-day shift details (MONDAY through SUNDAY).
     * Any day without an active schedule is explicitly set to "OFF".
     */
    public Map<String, String> buildFull7DaysShiftDetails(DoctorClinicAffiliationEntity affiliation) {
        Map<String, String> shifts = new LinkedHashMap<>(7);

        shifts.put("MONDAY", formatShift(affiliation.getMondayStart(), affiliation.getMondayEnd()));
        shifts.put("TUESDAY", formatShift(affiliation.getTuesdayStart(), affiliation.getTuesdayEnd()));
        shifts.put("WEDNESDAY", formatShift(affiliation.getWednesdayStart(), affiliation.getWednesdayEnd()));
        shifts.put("THURSDAY", formatShift(affiliation.getThursdayStart(), affiliation.getThursdayEnd()));
        shifts.put("FRIDAY", formatShift(affiliation.getFridayStart(), affiliation.getFridayEnd()));
        shifts.put("SATURDAY", formatShift(affiliation.getSaturdayStart(), affiliation.getSaturdayEnd()));
        shifts.put("SUNDAY", formatShift(affiliation.getSundayStart(), affiliation.getSundayEnd()));

        return shifts;
    }

    private String formatShift(LocalTime start, LocalTime end) {
        if (start != null && end != null) {
            return start.format(TIME_FORMATTER) + " - " + end.format(TIME_FORMATTER);
        }
        return "OFF";
    }

    private AffiliationResponseDTO buildResponse(DoctorClinicAffiliationEntity affiliation, String message) {
        UUID docId = null;
        String docName = null;
        if (affiliation.getDoctor() != null) {
            docId = affiliation.getDoctor().getId();
            docName = ((affiliation.getDoctor().getFirstName() != null ? affiliation.getDoctor().getFirstName() : "")
                    + " " + (affiliation.getDoctor().getLastName() != null ? affiliation.getDoctor().getLastName() : "")).trim();
        }

        UUID clinicId = null;
        String clinicName = null;
        String clinicAddress = null;
        if (affiliation.getClinic() != null) {
            clinicId = affiliation.getClinic().getId();
            clinicName = affiliation.getClinic().getName();
            clinicAddress = (affiliation.getClinic().getCity() != null ? affiliation.getClinic().getCity() : "")
                    + (affiliation.getClinic().getState() != null ? ", " + affiliation.getClinic().getState() : "");
            if (clinicAddress.startsWith(", ")) {
                clinicAddress = clinicAddress.substring(2);
            }
        }

        Map<String, String> fullShifts = buildFull7DaysShiftDetails(affiliation);

        return AffiliationResponseDTO.builder()
                .affiliationId(affiliation.getId())
                .status(affiliation.getStatus())
                .message(message)
                .doctorId(docId)
                .doctorName(docName)
                .clinicId(clinicId)
                .clinicName(clinicName)
                .clinicAddress(clinicAddress)
                .doctorCharge(affiliation.getDoctorCharge())
                .clinicCharge(affiliation.getClinicCharge())
                .initiatedBy(affiliation.getInitiatedBy())
                .actionRequiredBy(affiliation.getActionRequiredBy())
                .shiftDetails(fullShifts)
                .patientLimits(affiliation.getDailyPatientLimit())
                .joiningDate(affiliation.getJoiningDate())
                .version(affiliation.getVersion())
                .build();
    }
}