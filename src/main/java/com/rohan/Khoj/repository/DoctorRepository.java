package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    
    Optional<DoctorEntity> findByEmail(String email);
    
    Optional<DoctorEntity> findByRegistrationNumber(String registrationNumber);
    
    List<DoctorEntity> findBySpecializationsContaining(String specialization); // For multi-valued specializations
    
    List<DoctorEntity> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<DoctorEntity> findByLastNameContaining(String lastName);
}
