package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "doctors")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"clinicAffiliations", "appointments"})
@EqualsAndHashCode(callSuper = true, exclude = {"clinicAffiliations", "appointments"})
public class DoctorEntity extends BaseUserEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "gender", length = 10)
    private String gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_phone_numbers", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "phone_number", nullable = false, length = 20)
    private Set<String> phoneNumbers;

    @Column(name = "registration_number", unique = true, nullable = false, length = 50)
    private String registrationNumber;

    @Column(name = "registration_issue_date", nullable = false)
    private LocalDate registrationIssueDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_specialization", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "specialization", nullable = false, length = 100)
    private Set<String> specialization;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doctor_qualifications", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "qualifications", nullable = false, length = 100)
    private Set<String> qualifications;

    @Transient
    public Integer getYearsOfExperience() {
        if (registrationIssueDate != null) {
            return Period.between(registrationIssueDate, LocalDate.now()).getYears();
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(Role.ROLE_DOCTOR); // Doctor always has ROLE_DOCTOR
    }

    @Override
    public UserType getUserType() {
        return UserType.DOCTOR; // Or DOCTOR, CLINIC as appropriate
    }

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("doctor-affiliations") // This side will be serialized
    private Set<DoctorClinicAffiliationEntity> clinicAffiliations;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("doctor-appointments") // This side will be serialized
    private Set<AppointmentDetailEntity> appointments;

}
