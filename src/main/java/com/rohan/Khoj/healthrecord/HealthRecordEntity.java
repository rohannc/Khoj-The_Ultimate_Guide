package com.rohan.Khoj.healthrecord;

import com.rohan.Khoj.patient.PatientEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    private String documentTitle;
    private String documentType; // e.g., LAB_REPORT, MRI, PRESCRIPTION_SCAN
    private String documentUrl;
    
    @Column(name = "test_date")
    private LocalDate testDate;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
}
