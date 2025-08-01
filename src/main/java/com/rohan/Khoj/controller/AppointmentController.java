package com.rohan.Khoj.controller;

import com.rohan.Khoj.customException.BadRequestException;
import com.rohan.Khoj.customException.ConflictException;
import com.rohan.Khoj.customException.ResourceNotFoundException;
import com.rohan.Khoj.dto.AppointmentDTO; // Response DTO
import com.rohan.Khoj.dto.AppointmentRequestDTO; // Request DTO for creation
import com.rohan.Khoj.dto.AppointmentUpdateRequestDTO; // Request DTO for updates
import com.rohan.Khoj.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // For DTO validation
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class AppointmentController {

    private final AppointmentService appointmentService; // Injects the AppointmentService dependency

    /**
     * Schedules a new appointment.
     * @param request The DTO containing appointment details.
     * @return ResponseEntity with the created AppointmentDTO and HttpStatus.CREATED.
     * @throws ResourceNotFoundException if patient, doctor, or clinic not found.
     * @throws BadRequestException for business rule violations (e.g., availability).
     * @throws ConflictException for double-booking or other conflicts.
     */
    @PostMapping
    public ResponseEntity<AppointmentDTO> scheduleAppointment(@Valid @RequestBody AppointmentRequestDTO request) {
        try {
            AppointmentDTO scheduledAppointment = appointmentService.scheduleAppointment(request);

            return new ResponseEntity<>(scheduledAppointment, HttpStatus.CREATED);
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e; // Let @ResponseStatus handle 404/409
        } catch (IllegalArgumentException e) { // Catch potential service-thrown IllegalArgumentException for 400
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error scheduling appointment: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all appointments.
     * @return ResponseEntity with a list of AppointmentDTO and HttpStatus.OK, or HttpStatus.NO_CONTENT if none found.
     */
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    /**
     * Retrieves an appointment by its ID.
     * @param id The UUID of the appointment.
     * @return ResponseEntity with AppointmentDTO and HttpStatus.OK, or HttpStatus.NOT_FOUND if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable UUID id) {
        return appointmentService.getAppointmentById(id)
                .map(appointment -> new ResponseEntity<>(appointment, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    /**
     * Retrieves appointments for a specific patient.
     * @param patientId The UUID of the patient.
     * @return ResponseEntity with a list of AppointmentDTO, or HttpStatus.NO_CONTENT if none found.
     * @throws ResourceNotFoundException if the patient is not found.
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForPatient(@PathVariable UUID patientId) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsForPatient(patientId);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error fetching appointments for patient " + patientId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves appointments for a specific doctor.
     * @param doctorId The UUID of the doctor.
     * @return ResponseEntity with a list of AppointmentDTO, or HttpStatus.NO_CONTENT if none found.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForDoctor(@PathVariable UUID doctorId) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsForDoctor(doctorId);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error fetching appointments for doctor " + doctorId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves appointments for a specific clinic.
     * @param clinicId The UUID of the clinic.
     * @return ResponseEntity with a list of AppointmentDTO, or HttpStatus.NO_CONTENT if none found.
     * @throws ResourceNotFoundException if the clinic is not found.
     */
    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForClinic(@PathVariable UUID clinicId) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsForClinic(clinicId);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error fetching appointments for clinic " + clinicId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves appointments by status.
     * @param status The status to search for (e.g., "Scheduled", "Cancelled").
     * @return ResponseEntity with a list of AppointmentDTO, or HttpStatus.NO_CONTENT if none found.
     */
    @GetMapping("/by-status")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(@RequestParam String status) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByStatus(status);
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    /**
     * Retrieves appointments for a specific doctor on a specific date.
     * @param doctorId The UUID of the doctor.
     * @param date The date of the appointment (format YYYY-MM-DD).
     * @return ResponseEntity with a list of AppointmentDTO, or HttpStatus.NO_CONTENT if none found.
     * @throws ResourceNotFoundException if the doctor is not found.
     */
    @GetMapping("/doctor/{doctorId}/date")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForDoctorOnDate(
            @PathVariable UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsForDoctorOnDate(doctorId, date);
            if (appointments.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error fetching appointments for doctor " + doctorId + " on " + date + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing appointment.
     * @param id The UUID of the appointment to update.
     * @param updateRequest The DTO containing the updated appointment details.
     * @return ResponseEntity with the updated AppointmentDTO and HttpStatus.OK.
     * @throws ResourceNotFoundException if the appointment is not found.
     * @throws BadRequestException for invalid update data or business rule violations.
     * @throws ConflictException for conflicts during update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable UUID id, @Valid @RequestBody AppointmentUpdateRequestDTO updateRequest) {
        try {
            AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, updateRequest);
            return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
        } catch (ResourceNotFoundException | ConflictException e) {
            throw e; // Let @ResponseStatus handle 404/409
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error updating appointment " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes an appointment.
     * @param id The UUID of the appointment to delete.
     * @return ResponseEntity with the AppointmentDTO of the deleted appointment and HttpStatus.OK.
     * Or HttpStatus.NOT_FOUND if the appointment does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentDTO> deleteAppointment(@PathVariable UUID id) {
        try {
            AppointmentDTO deletedAppointment = appointmentService.deleteAppointment(id);
            return new ResponseEntity<>(deletedAppointment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e; // Let @ResponseStatus handle 404
        } catch (Exception e) {
            System.err.println("Error deleting appointment " + id + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}