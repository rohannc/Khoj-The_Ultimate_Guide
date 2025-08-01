package com.rohan.Khoj.repository;

import com.rohan.Khoj.embeddable.DoctorClinicAffiliationId;
import com.rohan.Khoj.entity.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.entity.ClinicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorClinicAffiliationRepository extends JpaRepository<DoctorClinicAffiliationEntity, DoctorClinicAffiliationId> {
    Optional<DoctorClinicAffiliationEntity> findByDoctorAndClinic(DoctorEntity doctor, ClinicEntity clinic);
}