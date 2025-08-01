package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.DoctorEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, UUID> {
    
    Optional<DoctorEntity> findByEmailId(String emailId);

    @Override
    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    Optional<DoctorEntity> findById(UUID Id);

    Optional<DoctorEntity> findByUsername(String doctorname);
    
    Optional<DoctorEntity> findByRegistrationNumber(String registrationNumber);
    
    List<DoctorEntity> findBySpecializationContaining(String specialization); // For multi-valued specializations
    
    List<DoctorEntity> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<DoctorEntity> findByLastNameContaining(String lastName);

    // Custom query method: Check if a doctor with a given email ID already exists
    boolean existsByEmailId(String emailId);

}
