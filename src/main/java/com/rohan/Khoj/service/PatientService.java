package com.rohan.Khoj.service;

import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // --- CRUD Operations ---

    @Transactional
    public PatientEntity createPatient(PatientEntity patient) {
        if (patient.getEmail() != null && patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Patient with this email already exists.");
        }
        return patientRepository.save(patient);
    }

    public List<PatientEntity> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<PatientEntity> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Transactional
    public PatientEntity updatePatient(Long id, PatientEntity updatedPatient) {
        return patientRepository.findById(id).map(patient -> {
            patient.setFirstName(updatedPatient.getFirstName());
            patient.setLastName(updatedPatient.getLastName());
            patient.setDateOfBirth(updatedPatient.getDateOfBirth());
            patient.setGender(updatedPatient.getGender());
            patient.setStreet(updatedPatient.getStreet());
            patient.setCity(updatedPatient.getCity());
            patient.setState(updatedPatient.getState());
            patient.setPinCode(updatedPatient.getPinCode());
            patient.setCountry(updatedPatient.getCountry());
            patient.setPhoneNumbers(updatedPatient.getPhoneNumbers());

            if (updatedPatient.getEmail() != null && !updatedPatient.getEmail().equals(patient.getEmail())) {
                if (patientRepository.findByEmail(updatedPatient.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("New email already in use by another patient.");
                }
                patient.setEmail(updatedPatient.getEmail());
            }
            patient.setBloodGroup(updatedPatient.getBloodGroup());

            return patientRepository.save(patient);
        }).orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }

    // --- Custom Business Logic ---

    public Optional<PatientEntity> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public List<PatientEntity> getPatientsByCity(String city) {
        return patientRepository.findByCity(city);
    }

    public List<PatientEntity> getPatientsByBloodGroup(String bloodGroup) {
        return patientRepository.findByBloodGroup(bloodGroup);
    }

}
