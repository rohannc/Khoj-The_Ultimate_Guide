package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.AppointmentDetailEntity;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.entity.PatientEntity;
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
    List<AppointmentDetailEntity> findByDoctor(DoctorEntity doctor);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByClinic(ClinicEntity clinic);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByDoctorAndAppointmentDate(DoctorEntity doctor, LocalDate appointmentDate);

    @EntityGraph(value = "appointment-with-details", type = EntityGraph.EntityGraphType.FETCH)
    List<AppointmentDetailEntity> findByStatus(String status);

    /**
     * Counts the number of appointments for a specific doctor and clinic in a given time slot.
     * This is used to check if a slot's patient limit has been reached.
     *
     * @param doctor  The DoctorEntity associated with the appointments.
     * @param clinic  The ClinicEntity where the appointments are held.
     * @param slotKey The time slot identifier (e.g., "MONDAY_09:00").
     * @return The number of appointments found for the given criteria.
     */
    long countByDoctorAndClinicAndAppointmentTimeSlot(DoctorEntity doctor, ClinicEntity clinic, String slotKey);

    /**
     * Counts the number of appointments for a specific patient, doctor, and clinic in a given time slot.
     * This is used to prevent double-booking.
     *
     * @param patientId The UUID of the patient.
     * @param doctorId  The UUID of the doctor.
     * @param clinicId  The UUID of the clinic.
     * @param slotKey   The time slot identifier (e.g., "MONDAY_09:00").
     * @return The number of appointments found for the given criteria.
     */
    long countByPatientIdAndDoctorIdAndClinicIdAndAppointmentTimeSlot(UUID patientId, UUID doctorId, UUID clinicId, String slotKey);
}