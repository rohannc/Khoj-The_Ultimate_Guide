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
}