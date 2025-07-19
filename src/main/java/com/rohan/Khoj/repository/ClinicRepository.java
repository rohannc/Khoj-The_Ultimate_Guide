package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.ClinicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<ClinicEntity, Long> {

    // Custom query method: Find a clinic by its name (exact match)
    Optional<ClinicEntity> findByName(String name);

    // Custom query method: Find clinics by city
    List<ClinicEntity> findByCity(String city);

    // Custom query method: Find clinics by zip code
    List<ClinicEntity> findByPinCode(String pinCode);

    // Custom query method: Check if a clinic with a given email already exists
    boolean existsByEmail(String email);
}
