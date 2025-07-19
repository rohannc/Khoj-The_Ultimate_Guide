package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    Optional<PatientEntity> findByEmail(String email);

    List<PatientEntity> findByFirstNameAndLastName(String firstName, String lastName);

    List<PatientEntity> findByCity(String city);

    List<PatientEntity> findByBloodGroup(String bloodGroup);
}
