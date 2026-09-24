package com.rohan.Khoj.doctor;

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

import com.rohan.Khoj.appointment.AppointmentDetailEntity;
import com.rohan.Khoj.common.Role;
import com.rohan.Khoj.common.BaseUserEntity;
import com.rohan.Khoj.common.UserType;
import com.rohan.Khoj.common.Gender;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;

@Data
@Entity
@Table(name = "doctors")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"clinicAffiliations"})
@EqualsAndHashCode(callSuper = true, exclude = {"clinicAffiliations"})
public class DoctorEntity extends BaseUserEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "registration_number", unique = true, nullable = false, length = 50)
    private String registrationNumber;

    @Column(name = "registration_issue_date", nullable = false)
    private LocalDate registrationIssueDate;

    @Column(name = "specializations", nullable = false, length = 255)
    private String specializations;

    @Column(name = "qualifications", nullable = false, length = 255)
    private String qualifications;

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



}
