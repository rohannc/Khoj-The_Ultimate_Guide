package com.rohan.Khoj.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "clinics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doctorAffiliations", "appointments"})
@EqualsAndHashCode(exclude = {"doctorAffiliations", "appointments"})
public class ClinicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

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
    // This creates a separate table (e.g., clinic_phone_numbers) linked by clinic_id.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "clinic_phone_numbers", joinColumns = @JoinColumn(name = "clinic_id"))
    @Column(name = "phone_number", nullable = false, length = 20)
    private Set<String> phoneNumbers;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "website", length = 255)
    private String website;

    // OpeningHours: Can be complex. For simplicity, let's use a JSON String or a Map for now.
    // If a dedicated table for daily hours is needed, it would be another @OneToMany.
    // Here, we model it as a simple text or a Map (requires JSON capabilities in DB or custom converter).
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "clinic_opening_hours", joinColumns = @JoinColumn(name = "clinic_id"))
    @MapKeyColumn(name = "day_of_week", length = 20)
    @Column(name = "hours", length = 100)
    private Map<String, String> openingHours; // e.g., {"Monday": "9AM-5PM", "Tuesday": "9AM-5PM"}

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("clinic-affiliations") // This side will be serialized
    private Set<DoctorClinicAffiliationEntity> doctorAffiliations;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("clinic-appointments") // This side will be serialized
    private Set<AppointmentDetailEntity> appointments;

}
