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
     * Counts the number of appointments for a specific affiliation in a given time slot.
     * This is used to check if a slot's patient limit has been reached.
     *
     * @param affiliation The DoctorClinicAffiliationEntity associated with the appointments.
     * @param slotKey The time slot identifier (e.g., "MONDAY_09:00").
     * @return The number of appointments found for the given criteria.
     */
    long countByAffiliationAndAppointmentTimeSlot(DoctorClinicAffiliationEntity affiliation, String slotKey);

    /**
     * Counts the number of appointments for a specific patient and affiliation in a given time slot.
     * This is used to prevent double-booking.
     *
     * @param patientId The UUID of the patient.
     * @param affiliationId The UUID of the affiliation.
     * @param slotKey   The time slot identifier (e.g., "MONDAY_09:00").
     * @return The number of appointments found for the given criteria.
     */
    long countByPatientIdAndAffiliationIdAndAppointmentTimeSlot(UUID patientId, UUID affiliationId, String slotKey);
}