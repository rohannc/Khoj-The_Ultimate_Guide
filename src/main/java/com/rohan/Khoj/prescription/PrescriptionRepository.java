package com.rohan.Khoj.prescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<PrescriptionEntity, UUID> {
    List<PrescriptionEntity> findByPatientIdAndIsActiveTrueOrderByIssuedAtDesc(UUID patientId);
    List<PrescriptionEntity> findByPatientId(UUID patientId);
}
