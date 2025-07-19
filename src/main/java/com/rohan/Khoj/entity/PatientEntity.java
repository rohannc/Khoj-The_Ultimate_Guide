package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"appointments"})
@EqualsAndHashCode(exclude = {"appointments"})
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

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
    private String country = "India";

    // Multivalued Phone numbers: Using @ElementCollection for simplicity.
    // This creates a separate table (e.g., patient_phone_numbers) linked by patient_id.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "patient_phone_numbers", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "phone_number", nullable = false, length = 20)
    private Set<String> phoneNumbers;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "blood_group", length = 5) // e.g., "A+", "O-"
    private String bloodGroup;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("patient-appointments") // This side will be serialized
    private Set<AppointmentDetailEntity> appointments;

}
