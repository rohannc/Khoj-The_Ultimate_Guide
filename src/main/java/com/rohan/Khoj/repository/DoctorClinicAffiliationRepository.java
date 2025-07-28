package com.rohan.Khoj.repository;

import com.rohan.Khoj.dto.ClinicDTO;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface DoctorClinicAffiliationRepository extends JpaRepository<DoctorClinicAffiliationEntity, UUID> {

    // Find all affiliations for a specific doctor
    List<DoctorClinicAffiliationEntity> findByDoctor(DoctorEntity doctor);

    // Find all affiliations for a specific clinic
    List<DoctorClinicAffiliationEntity> findByClinic(ClinicEntity clinic);

    // Find a specific affiliation by doctor and clinic
    Optional<DoctorClinicAffiliationEntity> findByDoctorAndClinic(DoctorEntity doctor, ClinicEntity clinic);

    // Check if a doctor is already affiliated with a clinic
    boolean existsByDoctorAndClinic(DoctorEntity doctor, ClinicEntity clinic);

}
