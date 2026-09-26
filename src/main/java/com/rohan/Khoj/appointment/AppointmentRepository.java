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
}