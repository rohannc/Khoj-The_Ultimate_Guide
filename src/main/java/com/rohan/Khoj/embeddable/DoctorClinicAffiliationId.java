package com.rohan.Khoj.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Embeddable // Marks this class as embeddable within an entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorClinicAffiliationId implements Serializable {

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "clinic_id")
    private Long clinicId;
}
