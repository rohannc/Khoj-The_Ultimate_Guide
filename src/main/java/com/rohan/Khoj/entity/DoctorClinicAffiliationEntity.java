package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "doctor_clinic_affiliation") // Junction table for M:N relationship
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctor", "clinic"})
@EqualsAndHashCode(exclude = {"doctor", "clinic"})
public class DoctorClinicAffiliationEntity implements Serializable { // Serializable for composite primary key

    @EmbeddedId
    private DoctorClinicAffiliationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("doctorId")
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-affiliations") // This side will NOT be serialized
    private DoctorEntity doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clinicId")
    @JoinColumn(name = "clinic_id", nullable = false)
    @JsonBackReference("clinic-affiliations") // This side will NOT be serialized
    private ClinicEntity clinic;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "role_in_clinic", length = 50) // e.g., "Full-time", "Part-time"
    private String roleInClinic;

    @Column(name = "shift_details", columnDefinition = "TEXT") // Could be more structured
    private String shiftDetails; // e.g., "Mon-Wed 9AM-1PM"

    @Column(name = "charge", nullable = false, length = 10)
    private double charge;

    // Helper constructor to simplify creation
    public DoctorClinicAffiliationEntity(DoctorEntity doctor, ClinicEntity clinic, LocalDate joiningDate, String roleInClinic, String shiftDetails, double charge) {
        this.doctor = doctor;
        this.clinic = clinic;
        this.joiningDate = joiningDate;
        this.roleInClinic = roleInClinic;
        this.shiftDetails = shiftDetails;
        this.charge = charge;
        this.id = new DoctorClinicAffiliationId(doctor.getId(), clinic.getId());
    }
}