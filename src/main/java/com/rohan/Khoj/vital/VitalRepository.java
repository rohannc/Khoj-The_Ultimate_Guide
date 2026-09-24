package com.rohan.Khoj.vital;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VitalRepository extends JpaRepository<VitalEntity, UUID> {
    List<VitalEntity> findByPatientIdOrderByRecordedAtDesc(UUID patientId, Pageable pageable);
}
