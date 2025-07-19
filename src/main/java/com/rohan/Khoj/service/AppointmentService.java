package com.rohan.Khoj.service;

import com.rohan.Khoj.entity.AppointmentDetailEntity;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientService patientService; // To fetch Patient object
    @Autowired
    private DoctorService doctorService;   // To fetch Doctor object
    @Autowired
    private ClinicService clinicService;   // To fetch Clinic object

    // --- CRUD Operations ---

    @Transactional
    public AppointmentDetailEntity scheduleAppointment(AppointmentDetailEntity appointment) {
        // Business logic: Ensure associated entities exist and are valid
        PatientEntity patient = patientService.getPatientById(appointment.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + appointment.getPatient().getId()));
        DoctorEntity doctor = doctorService.getDoctorById(appointment.getDoctor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + appointment.getDoctor().getId()));
        ClinicEntity clinic = clinicService.getClinicById(appointment.getClinic().getId())
                .orElseThrow(() -> new IllegalArgumentException("Clinic not found with ID: " + appointment.getClinic().getId()));

        // Set the fetched managed entities back to the appointment object
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setClinic(clinic);

        // Further validation: Check doctor's availability, clinic's hours, prevent double booking etc.
        // This can be complex and depends on specific business rules.
        // For example:
        // if (!doctorService.isDoctorAvailable(doctor.getId(), appointment.getAppointmentDate(), appointment.getAppointmentTime())) {
        //     throw new IllegalArgumentException("Doctor is not available at this time.");
        // }

        appointment.setStatus("Scheduled"); // Default status for new appointments
        return appointmentRepository.save(appointment);
    }

    public List<AppointmentDetailEntity> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<AppointmentDetailEntity> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Transactional
    public AppointmentDetailEntity updateAppointment(Long id, AppointmentDetailEntity updatedAppointment) {
        return appointmentRepository.findById(id).map(appointment -> {
            // Update fields
            appointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
            appointment.setAppointmentTime(updatedAppointment.getAppointmentTime());
            appointment.setStatus(updatedAppointment.getStatus());

            // If patient, doctor, or clinic are updated, fetch and set managed entities
            if (updatedAppointment.getPatient() != null && !updatedAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {
                PatientEntity newPatient = patientService.getPatientById(updatedAppointment.getPatient().getId())
                        .orElseThrow(() -> new IllegalArgumentException("New Patient not found with ID: " + updatedAppointment.getPatient().getId()));
                appointment.setPatient(newPatient);
            }
            if (updatedAppointment.getDoctor() != null && !updatedAppointment.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                DoctorEntity newDoctor = doctorService.getDoctorById(updatedAppointment.getDoctor().getId())
                        .orElseThrow(() -> new IllegalArgumentException("New Doctor not found with ID: " + updatedAppointment.getDoctor().getId()));
                appointment.setDoctor(newDoctor);
            }
            if (updatedAppointment.getClinic() != null && !updatedAppointment.getClinic().getId().equals(appointment.getClinic().getId())) {
                ClinicEntity newClinic = clinicService.getClinicById(updatedAppointment.getClinic().getId())
                        .orElseThrow(() -> new IllegalArgumentException("New Clinic not found with ID: " + updatedAppointment.getClinic().getId()));
                appointment.setClinic(newClinic);
            }

            return appointmentRepository.save(appointment);
        }).orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    // --- Custom Business Logic ---

    public List<AppointmentDetailEntity> getAppointmentsForPatient(Long patientId) {
        PatientEntity patient = patientService.getPatientById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        return appointmentRepository.findByPatient(patient);
    }

    public List<AppointmentDetailEntity> getAppointmentsForDoctor(Long doctorId) {
        DoctorEntity doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        return appointmentRepository.findByDoctor(doctor);
    }

    public List<AppointmentDetailEntity> getAppointmentsForClinic(Long clinicId) {
        ClinicEntity clinic = clinicService.getClinicById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + clinicId));
        return appointmentRepository.findByClinic(clinic);
    }

    public List<AppointmentDetailEntity> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public List<AppointmentDetailEntity> getAppointmentsForDoctorOnDate(Long doctorId, LocalDate date) {
        DoctorEntity doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        return appointmentRepository.findByDoctorAndAppointmentDate(doctor, date);
    }

}
