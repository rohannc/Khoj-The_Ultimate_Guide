package com.rohan.Khoj.appointment;

import com.rohan.Khoj.appointment.AppointmentDetailEntity;
import com.rohan.Khoj.affiliation.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.patient.PatientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<AppointmentDetailEntity, UUID> {

    @Override
    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    Optional<AppointmentDetailEntity> findById(UUID id);

    @Override
    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findAll();

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByPatient(PatientEntity patient);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByAffiliation(DoctorClinicAffiliationEntity affiliation);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByAffiliationAndAppointmentDate(DoctorClinicAffiliationEntity affiliation, LocalDate appointmentDate);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByStatus(String status);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByPatientIdAndStatusOrderByAppointmentDateAsc(UUID patientId, String status, org.springframework.data.domain.Pageable pageable);

    /**
     * Counts the number of appointments for a specific affiliation on a given date.
     * Used to verify that the daily patient limit has not been exceeded.
     */
    long countByAffiliationAndAppointmentDate(DoctorClinicAffiliationEntity affiliation, LocalDate appointmentDate);

    /**
     * Checks if a patient already has an active appointment with this affiliation on a given date.
     */
    long countByPatientIdAndAffiliationIdAndAppointmentDate(UUID patientId, UUID affiliationId, LocalDate appointmentDate);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AppointmentDetailEntity a WHERE a.affiliation.doctor.id = :doctorId ORDER BY a.appointmentDate DESC, a.appointmentTime ASC")
    List<AppointmentDetailEntity> findByDoctorId(@org.springframework.data.repository.query.Param("doctorId") UUID doctorId);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AppointmentDetailEntity a WHERE a.affiliation.doctor.id = :doctorId AND a.appointmentDate = :date ORDER BY a.appointmentTime ASC, a.tokenNumber ASC")
    List<AppointmentDetailEntity> findByDoctorIdAndDate(
            @org.springframework.data.repository.query.Param("doctorId") UUID doctorId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AppointmentDetailEntity a WHERE a.affiliation.clinic.id = :clinicId ORDER BY a.appointmentDate DESC, a.appointmentTime ASC")
    List<AppointmentDetailEntity> findByClinicId(@org.springframework.data.repository.query.Param("clinicId") UUID clinicId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM AppointmentDetailEntity a WHERE a.affiliation.doctor.id = :doctorId")
    long countByDoctorId(@org.springframework.data.repository.query.Param("doctorId") UUID doctorId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM AppointmentDetailEntity a WHERE a.affiliation.doctor.id = :doctorId AND a.appointmentDate = :date")
    long countByDoctorIdAndDate(
            @org.springframework.data.repository.query.Param("doctorId") UUID doctorId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT a.patient.id) FROM AppointmentDetailEntity a WHERE a.affiliation.doctor.id = :doctorId")
    long countDistinctPatientsByDoctorId(@org.springframework.data.repository.query.Param("doctorId") UUID doctorId);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    @org.springframework.data.jpa.repository.Query("SELECT a FROM AppointmentDetailEntity a WHERE a.affiliation.clinic.id = :clinicId AND a.appointmentDate = :date ORDER BY a.appointmentTime ASC, a.tokenNumber ASC")
    List<AppointmentDetailEntity> findByClinicIdAndDate(
            @org.springframework.data.repository.query.Param("clinicId") UUID clinicId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM AppointmentDetailEntity a WHERE a.affiliation.clinic.id = :clinicId")
    long countByClinicId(@org.springframework.data.repository.query.Param("clinicId") UUID clinicId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM AppointmentDetailEntity a WHERE a.affiliation.clinic.id = :clinicId AND a.appointmentDate = :date")
    long countByClinicIdAndDate(
            @org.springframework.data.repository.query.Param("clinicId") UUID clinicId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT a.patient.id) FROM AppointmentDetailEntity a WHERE a.affiliation.clinic.id = :clinicId")
    long countDistinctPatientsByClinicId(@org.springframework.data.repository.query.Param("clinicId") UUID clinicId);
}