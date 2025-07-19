package com.rohan.Khoj.service;

import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorClinicAffiliationEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.repository.DoctorClinicAffiliationRepository;
import com.rohan.Khoj.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorClinicAffiliationRepository affiliationRepository;

    @Autowired
    private ClinicService clinicService; // Inject ClinicService to fetch Clinic objects

    // --- CRUD Operations ---

    @Transactional
    public DoctorEntity createDoctor(DoctorEntity doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Doctor with this email already exists.");
        }
        if (doctorRepository.findByRegistrationNumber(doctor.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("Doctor with this license number already exists.");
        }
        return doctorRepository.save(doctor);
    }

    public List<DoctorEntity> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<DoctorEntity> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    @Transactional
    public DoctorEntity updateDoctor(Long id, DoctorEntity updatedDoctor) {
        return doctorRepository.findById(id).map(doctor -> {
            doctor.setFirstName(updatedDoctor.getFirstName());
            doctor.setLastName(updatedDoctor.getLastName());
            doctor.setDateOfBirth(updatedDoctor.getDateOfBirth());
            doctor.setGender(updatedDoctor.getGender());
            doctor.setPhoneNumbers(updatedDoctor.getPhoneNumbers());

            if (updatedDoctor.getEmail() != null && !updatedDoctor.getEmail().equals(doctor.getEmail())) {
                if (doctorRepository.findByEmail(updatedDoctor.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("New email already in use by another doctor.");
                }
                doctor.setEmail(updatedDoctor.getEmail());
            }
            if (updatedDoctor.getRegistrationNumber() != null && !updatedDoctor.getRegistrationNumber().equals(doctor.getRegistrationNumber())) {
                if (doctorRepository.findByRegistrationNumber(updatedDoctor.getRegistrationNumber()).isPresent()) {
                    throw new IllegalArgumentException("New license number already in use by another doctor.");
                }
                doctor.setRegistrationNumber(updatedDoctor.getRegistrationNumber());
            }
            doctor.setSpecializations(updatedDoctor.getSpecializations());
            doctor.setQualifications(updatedDoctor.getQualifications());

            return doctorRepository.save(doctor);
        }).orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    // --- Custom Business Logic ---

    public Optional<DoctorEntity> getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    public List<DoctorEntity> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationsContaining(specialization);
    }

    public List<DoctorEntity> getDoctorsByLastName(String lastName) {
        return doctorRepository.findByLastNameContaining(lastName);
    }

    /**
     * Affiliates a doctor with a clinic.
     * @param doctorId The ID of the doctor.
     * @param clinicId The ID of the clinic.
     * @param joiningDate The date the doctor joined the clinic.
     * @param roleInClinic The role of the doctor in the clinic (e.g., "Full-time").
     * @param shiftDetails Details about the doctor's shifts at this clinic.
     * @return The created DoctorClinicAffiliation object.
     */
    @Transactional
    public DoctorClinicAffiliationEntity affiliateDoctorToClinic(Long doctorId, Long clinicId, LocalDate joiningDate, String roleInClinic, String shiftDetails) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        ClinicEntity clinic = clinicService.getClinicById(clinicId) // Use clinicService to get Clinic
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + clinicId));

        if (affiliationRepository.existsByDoctorAndClinic(doctor, clinic)) {
            throw new IllegalArgumentException("Doctor " + doctor.getLastName() + " is already affiliated with clinic " + clinic.getName());
        }

        DoctorClinicAffiliationEntity affiliation = new DoctorClinicAffiliationEntity(doctor, clinic, joiningDate, roleInClinic, shiftDetails);
        return affiliationRepository.save(affiliation);
    }

    /**
     * Removes a doctor's affiliation with a clinic.
     * @param doctorId The ID of the doctor.
     * @param clinicId The ID of the clinic.
     */
    @Transactional
    public void removeDoctorAffiliation(Long doctorId, Long clinicId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        ClinicEntity clinic = clinicService.getClinicById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found with id: " + clinicId));

        DoctorClinicAffiliationEntity affiliation = affiliationRepository.findByDoctorAndClinic(doctor, clinic)
                .orElseThrow(() -> new RuntimeException("Affiliation not found between doctor " + doctorId + " and clinic " + clinicId));

        affiliationRepository.delete(affiliation);
    }

    /**
     * Get all clinics a specific doctor is affiliated with.
     * @param doctorId The ID of the doctor.
     * @return A list of clinics the doctor works at.
     */
    public List<ClinicEntity> getClinicsForDoctor(Long doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        return affiliationRepository.findByDoctor(doctor).stream()
                .map(DoctorClinicAffiliationEntity::getClinic)
                .collect(Collectors.toList());
    }

}
