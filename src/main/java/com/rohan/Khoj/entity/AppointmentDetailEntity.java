package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@NamedEntityGraph(
        name = "appointment-with-details",
        attributeNodes = {
                @NamedAttributeNode("patient"),
                @NamedAttributeNode("doctor"),
                @NamedAttributeNode("clinic")
        }
)
@Entity
@Builder
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"patient", "doctor", "clinic"})
@EqualsAndHashCode(exclude = {"patient", "doctor", "clinic", "version"})
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
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-appointments")
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    @JsonBackReference("clinic-appointments")
    private ClinicEntity clinic;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    // New field to store the calculated time slot key
    @Column(name = "appointment_time_slot", length = 50, nullable = false)
    private String appointmentTimeSlot;

    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "reason", length = 512, nullable = false)
    private String reason;

    @Version // Added for optimistic locking
    private Long version;
}