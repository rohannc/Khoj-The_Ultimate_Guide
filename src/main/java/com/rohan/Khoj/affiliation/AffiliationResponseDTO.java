package com.rohan.Khoj.affiliation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing detailed doctor-clinic affiliation and complete 7-day schedule")
public class AffiliationResponseDTO {

    @Schema(description = "Unique identifier of the affiliation", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID affiliationId;

    @Schema(description = "Current status of the affiliation contract: PENDING, APPROVED, REJECTED, TERMINATED", example = "APPROVED")
    private AffiliationStatus status;

    @Schema(description = "Status message or outcome description", example = "Affiliation request approved.")
    private String message;

    @Schema(description = "UUID of the doctor", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID doctorId;

    @Schema(description = "Full name of the doctor", example = "Dr. Test Doctor")
    private String doctorName;

    @Schema(description = "UUID of the clinic", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID clinicId;

    @Schema(description = "Name of the clinic", example = "Khoj Test Clinic")
    private String clinicName;

    @Schema(description = "Address / City of the clinic", example = "Mumbai, Maharashtra")
    private String clinicAddress;

    @Schema(description = "Doctor consultation fee", example = "500.00")
    private Double doctorCharge;

    @Schema(description = "Clinic facility charge", example = "100.00")
    private Double clinicCharge;

    @Schema(description = "Party that initiated the request or latest counter-offer: DOCTOR or CLINIC", example = "DOCTOR")
    private AffiliationRequestInitiator initiatedBy;

    @Schema(description = "Party whose turn it is to take action: DOCTOR or CLINIC", example = "CLINIC")
    private AffiliationActionRequiredBy actionRequiredBy;

    @Schema(
            description = "Complete 7-day weekly shift schedule (MONDAY through SUNDAY). Active days show 'HH:mm - HH:mm'; off days show 'OFF'.",
            example = "{\"MONDAY\": \"09:00 - 17:00\", \"TUESDAY\": \"09:00 - 17:00\", \"WEDNESDAY\": \"09:00 - 17:00\", \"THURSDAY\": \"09:00 - 17:00\", \"FRIDAY\": \"09:00 - 17:00\", \"SATURDAY\": \"10:00 - 14:00\", \"SUNDAY\": \"OFF\"}"
    )
    private Map<String, String> shiftDetails;

    @Schema(description = "Contract joining / effective date", example = "2025-01-15")
    private LocalDate joiningDate;

    @Schema(description = "Optimistic locking version", example = "0")
    private Long version;

    @Schema(description = "Max daily patient appointment limit", example = "20")
    private Integer patientLimits;
}