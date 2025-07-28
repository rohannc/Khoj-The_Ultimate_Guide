package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {
    
    Optional<DoctorEntity> findByEmailId(String emailId);

    Optional<DoctorEntity> findByUsername(String doctorname);
    
    Optional<DoctorEntity> findByRegistrationNumber(String registrationNumber);
    
    List<DoctorEntity> findBySpecializationsContaining(String specialization); // For multi-valued specializations
    
    List<DoctorEntity> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<DoctorEntity> findByLastNameContaining(String lastName);

    // Custom query method: Check if a doctor with a given email ID already exists
    boolean existsByEmailId(String emailId);

}
