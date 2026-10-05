package com.rohan.Khoj.doctor;

import com.rohan.Khoj.doctor.DoctorEntity;
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
    
    List<DoctorEntity> findBySpecializationsContainingIgnoreCase(String keyword);
    
    List<DoctorEntity> findByQualificationsContainingIgnoreCase(String keyword);
    
    List<DoctorEntity> findByFirstNameAndLastName(String firstName, String lastName);
    
    List<DoctorEntity> findByLastNameContaining(String lastName);

    // Custom query method: Check if a doctor with a given email ID already exists
    boolean existsByEmailId(String emailId);

    // Kept to avoid compiler errors if other places use it, but renamed based on above.
    List<DoctorEntity> findBySpecializationsContainingIgnoreCase(String keyword, org.springframework.data.domain.Pageable pageable);

    List<DoctorEntity> findByLastNameContainingIgnoreCase(String lastName);
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT d FROM DoctorEntity d LEFT JOIN d.clinicAffiliations a LEFT JOIN a.clinic c " +
           "WHERE (CAST(:query AS string) IS NULL OR LOWER(d.firstName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))) " +
           "AND (CAST(:specialization AS string) IS NULL OR LOWER(d.specializations) LIKE LOWER(CONCAT('%', CAST(:specialization AS string), '%'))) " +
           "AND (CAST(:city AS string) IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', CAST(:city AS string), '%'))) " +
           "AND (:gender IS NULL OR d.gender = :gender)")
    org.springframework.data.domain.Page<DoctorEntity> searchDoctors(
            @org.springframework.data.repository.query.Param("query") String query, 
            @org.springframework.data.repository.query.Param("specialization") String specialization, 
            @org.springframework.data.repository.query.Param("city") String city, 
            @org.springframework.data.repository.query.Param("gender") com.rohan.Khoj.common.Gender gender, 
            org.springframework.data.domain.Pageable pageable);
}
