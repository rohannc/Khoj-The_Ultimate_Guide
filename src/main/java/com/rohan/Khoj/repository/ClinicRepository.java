package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.ClinicEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClinicRepository extends JpaRepository<ClinicEntity, UUID> {

    // Custom query method: Find a clinic by its name (exact match)
    Optional<ClinicEntity> findByName(String name);

    Optional<ClinicEntity> findByUsername(String username);

    // Custom query method: Find clinics by city
    List<ClinicEntity> findByCity(String city);

    // Custom query method: Find clinics by zip code
    List<ClinicEntity> findByPinCode(String pinCode);

    // Custom query method: Check if a clinic with a given email already exists
    boolean existsByEmailId(String emailId);

    Optional<ClinicEntity> findByEmailId(@Email(message = "Invalid email format") @Size(max = 255, message = "Email too long") String email);

    Optional<Object> findByCityContainingIgnoreCase(String city);
}
