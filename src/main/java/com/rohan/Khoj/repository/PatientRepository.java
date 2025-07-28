package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {

    Optional<PatientEntity> findByEmailId(String email);

    Optional<PatientEntity> findByUsername(String Patientname);

    List<PatientEntity> findByFirstNameAndLastName(String firstName, String lastName);

    List<PatientEntity> findByCity(String city);

    List<PatientEntity> findByBloodGroup(String bloodGroup);

    // Custom query method: Check if a patient with a given email ID already exists
    boolean existsByEmailId(String emailId);

}
