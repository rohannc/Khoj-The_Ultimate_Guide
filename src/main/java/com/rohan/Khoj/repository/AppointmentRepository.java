package com.rohan.Khoj.repository;

import com.rohan.Khoj.entity.AppointmentDetailEntity;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface AppointmentRepository extends JpaRepository<AppointmentDetailEntity, UUID> {

    // Find appointments for a specific patient
    List<AppointmentDetailEntity> findByPatient(PatientEntity patient);

    // Find appointments for a specific doctor
    List<AppointmentDetailEntity> findByDoctor(DoctorEntity doctor);

    // Find appointments for a specific clinic
    List<AppointmentDetailEntity> findByClinic(ClinicEntity clinic);

    // Find appointments by patient and date
    List<AppointmentDetailEntity> findByPatientAndAppointmentDate(PatientEntity patient, LocalDate appointmentDate);

    // Find appointments by doctor and date
    List<AppointmentDetailEntity> findByDoctorAndAppointmentDate(DoctorEntity doctor, LocalDate appointmentDate);


    // Find appointments by status
    List<AppointmentDetailEntity> findByStatus(String status);

}
