package com.rohan.Khoj.clinic;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.time.LocalTime;

import com.rohan.Khoj.appointment.AppointmentDetailEntity;
import com.rohan.Khoj.common.Role;
import com.rohan.Khoj.common.BaseUserEntity;
import com.rohan.Khoj.common.UserType;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;

@Data
@Entity
@Table(name = "clinics")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = {"doctorAffiliations"})
@EqualsAndHashCode(callSuper = true, exclude = {"doctorAffiliations"})
public class ClinicEntity extends BaseUserEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "street", length = 255)
    private String street;
    @Column(name = "city", length = 100)
    private String city;
    @Column(name = "state", length = 100)
    private String state;
    @Column(name = "pin_code", length = 6)
    private String pinCode;
    @Column(name = "country", length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'India'")
    private String country;

    @Column(name = "website", length = 255)
    private String website;

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

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("clinic-affiliations") // This side will be serialized
    private Set<DoctorClinicAffiliationEntity> doctorAffiliations;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(Role.ROLE_CLINIC); // Clinic always has ROLE_CLINIC
    }

    @Override
    public UserType getUserType() {
        return UserType.CLINIC; // Or DOCTOR, CLINIC as appropriate
    }
}
