package com.rohan.Khoj.appointment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;

@NamedEntityGraph(
        name = "appointment-with-details",
        attributeNodes = {
                @NamedAttributeNode("patient"),
                @NamedAttributeNode("affiliation")
        }
)
@Entity
@Builder
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"patient", "affiliation"})
@EqualsAndHashCode(exclude = {"patient", "affiliation", "version"})
public class AppointmentDetailEntity {

    @Id
    @Column(name = "appointment_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    protected UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference("patient-appointments")
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliation_id", nullable = false)
    private DoctorClinicAffiliationEntity affiliation;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    // New field to store the calculated time slot key
    @Column(name = "appointment_time_slot", length = 50)
    private String appointmentTimeSlot;

    @Column(name = "token_number")
    private Integer tokenNumber;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "reason", length = 512, nullable = false)
    private String reason;

    @Version // Added for optimistic locking
    private Long version;
}