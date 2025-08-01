package com.rohan.Khoj.entity;

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

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "street", length = 255)
    private String street;
    @Column(name = "city", length = 100)
    private String city;
    @Column(name = "state", length = 100)
    private String state;
    @Column(name = "pin_code", length = 20)
    private String pinCode;
    @Column(name = "country", length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'India'")
    private String country;

    // Multivalued Phone numbers: Using @ElementCollection for simplicity.
    // This creates a separate table (e.g., patient_phone_numbers) linked by patient_id.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "patient_phone_numbers", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "phone_number", nullable = false, length = 20)
    private Set<String> phoneNumbers;

    @Column(name = "blood_group", length = 5) // e.g., "A+", "O-"
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
