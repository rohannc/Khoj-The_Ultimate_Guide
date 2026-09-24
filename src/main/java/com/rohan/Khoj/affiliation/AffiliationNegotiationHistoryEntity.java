package com.rohan.Khoj.affiliation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "affiliation_negotiation_history")
public class AffiliationNegotiationHistoryEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false)
    private DoctorClinicAffiliationEntity affiliation;

    @Column(name = "doctor_charge")
    private Double doctorCharge;

    @Column(name = "clinic_charge")
    private Double clinicCharge;

    @Column(name = "daily_patient_limit")
    private Integer dailyPatientLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AffiliationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "actor")
    private AffiliationRequestInitiator actor; // Who made this change

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
