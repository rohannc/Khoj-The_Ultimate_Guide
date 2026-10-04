package com.rohan.Khoj.prescription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItemEntity, UUID> {
    List<PrescriptionItemEntity> findByPrescriptionPatientId(UUID patientId);
    List<PrescriptionItemEntity> findByPrescriptionDoctorId(UUID doctorId);
}
