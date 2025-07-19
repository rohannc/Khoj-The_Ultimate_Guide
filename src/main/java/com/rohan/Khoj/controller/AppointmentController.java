package com.rohan.Khoj.controller;

import com.rohan.Khoj.entity.AppointmentDetailEntity;
import com.rohan.Khoj.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // POST /api/appointments
    // Expects a request body like:
    // {
    //   "patient": {"id": 1},
    //   "doctor": {"id": 2},
    //   "clinic": {"id": 1},
    //   "appointmentDate": "2025-07-20",
    //   "appointmentTime": "10:30:00",
    //   "reasonForVisit": "Routine checkup"
    // }
    @PostMapping
    public ResponseEntity<AppointmentDetailEntity> scheduleAppointment(@RequestBody AppointmentDetailEntity appointment) {
        try {
            // Service will fetch full Patient, Doctor, Clinic objects based on IDs
            AppointmentDetailEntity scheduledAppointment = appointmentService.scheduleAppointment(appointment);
            return new ResponseEntity<>(scheduledAppointment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/appointments
    @GetMapping
    public ResponseEntity<List<AppointmentDetailEntity>> getAllAppointments() {
        List<AppointmentDetailEntity> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // GET /api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDetailEntity> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(appointment -> new ResponseEntity<>(appointment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // PUT /api/appointments/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDetailEntity> updateAppointment(@PathVariable Long id, @RequestBody AppointmentDetailEntity appointment) {
        try {
            AppointmentDetailEntity updatedAppointment = appointmentService.updateAppointment(id, appointment);
            return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/appointments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/appointments/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDetailEntity>> getAppointmentsForPatient(@PathVariable Long patientId) {
        try {
            List<AppointmentDetailEntity> appointments = appointmentService.getAppointmentsForPatient(patientId);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Patient not found
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/appointments/doctor/{doctorId}
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDetailEntity>> getAppointmentsForDoctor(@PathVariable Long doctorId) {
        try {
            List<AppointmentDetailEntity> appointments = appointmentService.getAppointmentsForDoctor(doctorId);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Doctor not found
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/appointments/clinic/{clinicId}
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<AppointmentDetailEntity>> getAppointmentsForClinic(@PathVariable Long clinicId) {
        try {
            List<AppointmentDetailEntity> appointments = appointmentService.getAppointmentsForClinic(clinicId);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Clinic not found
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/appointments/doctor/{doctorId}/date?date={date}
    @GetMapping("/doctor/{doctorId}/date")
    public ResponseEntity<List<AppointmentDetailEntity>> getAppointmentsForDoctorOnDate(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AppointmentDetailEntity> appointments = appointmentService.getAppointmentsForDoctorOnDate(doctorId, date);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Doctor not found
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/appointments/by-status?status={status}
    @GetMapping("/by-status")
    public ResponseEntity<List<AppointmentDetailEntity>> getAppointmentsByStatus(@RequestParam String status) {
        List<AppointmentDetailEntity> appointments = appointmentService.getAppointmentsByStatus(status);
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }
}