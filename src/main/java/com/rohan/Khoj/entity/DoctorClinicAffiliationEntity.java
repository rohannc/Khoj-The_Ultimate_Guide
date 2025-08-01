package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Column;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctor", "clinic"})
@EqualsAndHashCode(exclude = {"doctor", "clinic", "version"})
@Table(name = "doctor_clinic_affiliations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "clinic_id"}))
public class DoctorClinicAffiliationEntity implements Serializable {

    @EmbeddedId
    private DoctorClinicAffiliationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-affiliations")
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clinicId")
    @JoinColumn(name = "clinic_id", nullable = false)
    @JsonBackReference("clinic-affiliations")
    private ClinicEntity clinic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AffiliationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AffiliationRequestInitiator initiatedBy;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "doctor_charge")
    private Double doctorCharge;

    @Column(name = "clinic_charge")
    private Double clinicCharge;

    @ElementCollection
    @CollectionTable(
            name = "doctor_shift_details",
            joinColumns = {
                    // Correct order: doctorId first, then clinicId
                    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id"),
                    @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id")
            }
    )
    @MapKeyColumn(name = "day_of_week")
    @Column(name = "shift_time")
    private Map<String, String> shiftDetails;

    @Column(name = "patient_limits", nullable = false)
    private Integer patientLimits;

    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}