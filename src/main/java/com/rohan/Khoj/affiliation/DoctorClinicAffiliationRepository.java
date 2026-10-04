package com.rohan.Khoj.affiliation;

import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.clinic.ClinicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorClinicAffiliationRepository extends JpaRepository<DoctorClinicAffiliationEntity, UUID> {
    Optional<DoctorClinicAffiliationEntity> findByDoctorAndClinic(DoctorEntity doctor, ClinicEntity clinic);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM DoctorClinicAffiliationEntity a JOIN FETCH a.clinic JOIN FETCH a.doctor WHERE a.doctor.id = :doctorId AND (:status IS NULL OR a.status = :status)")
    java.util.List<DoctorClinicAffiliationEntity> findByDoctorIdAndStatus(
            @org.springframework.data.repository.query.Param("doctorId") UUID doctorId,
            @org.springframework.data.repository.query.Param("status") AffiliationStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM DoctorClinicAffiliationEntity a JOIN FETCH a.doctor JOIN FETCH a.clinic WHERE a.clinic.id = :clinicId AND (:status IS NULL OR a.status = :status)")
    java.util.List<DoctorClinicAffiliationEntity> findByClinicIdAndStatus(
            @org.springframework.data.repository.query.Param("clinicId") UUID clinicId,
            @org.springframework.data.repository.query.Param("status") AffiliationStatus status);
}