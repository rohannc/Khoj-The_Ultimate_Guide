package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"clinicAffiliations", "appointments"})
@EqualsAndHashCode(exclude = {"clinicAffiliations", "appointments"})
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 10)
    private String gender;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "doctor_phone_numbers", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "phone_number", nullable = false, length = 20)
    private Set<String> phoneNumbers;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "registration_number", unique = true, nullable = false, length = 50)
    private String registrationNumber;

    @Column(name = "registration_issue_date", nullable = false)
    private LocalDate registrationIssueDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_specializations", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "specialization", nullable = false, length = 100)
    private Set<String> specializations;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "doctor_qualifications", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "qualification", nullable = false, length = 100)
    private Set<String> qualifications;

    @Transient
    public Integer getYearsOfExperience() {
        if (registrationIssueDate != null) {
            return Period.between(registrationIssueDate, LocalDate.now()).getYears();
        }
        return null;
    }

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("doctor-affiliations") // This side will be serialized
    private Set<DoctorClinicAffiliationEntity> clinicAffiliations;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("doctor-appointments") // This side will be serialized
    private Set<AppointmentDetailEntity> appointments;

}
