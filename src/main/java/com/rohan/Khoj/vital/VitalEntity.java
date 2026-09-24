package com.rohan.Khoj.vital;

import com.rohan.Khoj.patient.PatientEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vitals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;
    private Integer heartRate;
    private Double weight;
    private Double temperature;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "bmi")
    private Double bmi;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime recordedAt;
}
