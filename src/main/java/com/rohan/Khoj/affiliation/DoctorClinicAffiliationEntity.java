package com.rohan.Khoj.affiliation;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.doctor.DoctorEntity;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctor", "clinic"})
@EqualsAndHashCode(exclude = {"doctor", "clinic", "version"})
@Table(name = "doctor_clinic_affiliations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "clinic_id"}))
public class DoctorClinicAffiliationEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-affiliations")
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "action_required_by")
    private AffiliationActionRequiredBy actionRequiredBy;

    @Column(name = "daily_patient_limit", nullable = false)
    private Integer dailyPatientLimit;

    @Column(name = "monday_start")
    private LocalTime mondayStart;
    @Column(name = "monday_end")
    private LocalTime mondayEnd;

    @Column(name = "tuesday_start")
    private LocalTime tuesdayStart;
    @Column(name = "tuesday_end")
    private LocalTime tuesdayEnd;

    @Column(name = "wednesday_start")
    private LocalTime wednesdayStart;
    @Column(name = "wednesday_end")
    private LocalTime wednesdayEnd;

    @Column(name = "thursday_start")
    private LocalTime thursdayStart;
    @Column(name = "thursday_end")
    private LocalTime thursdayEnd;

    @Column(name = "friday_start")
    private LocalTime fridayStart;
    @Column(name = "friday_end")
    private LocalTime fridayEnd;

    @Column(name = "saturday_start")
    private LocalTime saturdayStart;
    @Column(name = "saturday_end")
    private LocalTime saturdayEnd;

    @Column(name = "sunday_start")
    private LocalTime sundayStart;
    @Column(name = "sunday_end")
    private LocalTime sundayEnd;

    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}