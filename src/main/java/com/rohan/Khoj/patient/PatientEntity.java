package com.rohan.Khoj.patient;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.rohan.Khoj.appointment.AppointmentDetailEntity;
import com.rohan.Khoj.common.Role;
import com.rohan.Khoj.common.BaseUserEntity;
import com.rohan.Khoj.common.UserType;
import com.rohan.Khoj.common.Gender;

@Data
@Entity
@Table(name = "patients")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"appointments"})
@EqualsAndHashCode(callSuper = true, exclude = {"appointments"})
public class PatientEntity extends BaseUserEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

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

    @Column(name = "blood_group", length = 3) // e.g., "A+", "O-"
    private String bloodGroup;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(Role.ROLE_PATIENT); // Patient always has ROLE_PATIENT
    }

    @Override
    public UserType getUserType() {
        return UserType.PATIENT; // Or DOCTOR, CLINIC as appropriate
    }

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference("patient-appointments") // This side will be serialized
    private Set<AppointmentDetailEntity> appointments;

}
