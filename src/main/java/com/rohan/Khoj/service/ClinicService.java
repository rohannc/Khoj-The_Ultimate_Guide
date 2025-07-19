package com.rohan.Khoj.service;

import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClinicService {

    @Autowired // Injects the ClinicRepository
    private ClinicRepository clinicRepository;

    // --- CRUD Operations ---

    @Transactional // Override for write operations
    public ClinicEntity createClinic(ClinicEntity clinic) {
        // Add business logic here, e.g., validation
        if (clinicRepository.existsByEmail(clinic.getEmail())) {
            throw new IllegalArgumentException("Clinic with this email already exists.");
        }
        return clinicRepository.save(clinic);
    }

    public List<ClinicEntity> getAllClinics() {
        return clinicRepository.findAll();
    }

    public Optional<ClinicEntity> getClinicById(Long id) {
        return clinicRepository.findById(id);
    }

    @Transactional
    public ClinicEntity updateClinic(Long id, ClinicEntity updatedClinic) {
        return clinicRepository.findById(id).map(clinic -> {
            // Update fields as necessary
            clinic.setName(updatedClinic.getName());
            clinic.setStreet(updatedClinic.getStreet());
            clinic.setCity(updatedClinic.getCity());
            clinic.setState(updatedClinic.getState());
            clinic.setPinCode(updatedClinic.getPinCode());
            clinic.setCountry(updatedClinic.getCountry());
            clinic.setPhoneNumbers(updatedClinic.getPhoneNumbers());
            // Careful with updating unique email, might require additional checks
            if (updatedClinic.getEmail() != null && !updatedClinic.getEmail().equals(clinic.getEmail())) {
                if (clinicRepository.existsByEmail(updatedClinic.getEmail())) {
                    throw new IllegalArgumentException("New email already in use by another clinic.");
                }
                clinic.setEmail(updatedClinic.getEmail());
            }
            clinic.setWebsite(updatedClinic.getWebsite());
            clinic.setOpeningHours(updatedClinic.getOpeningHours());
            return clinicRepository.save(clinic);
        }).orElseThrow(() -> new RuntimeException("Clinic not found with id: " + id)); // Or throw custom exception
    }

    @Transactional
    public void deleteClinic(Long id) {
        if (!clinicRepository.existsById(id)) {
            throw new RuntimeException("Clinic not found with id: " + id);
        }
        clinicRepository.deleteById(id);
    }

    // --- Custom Query Methods (Business Logic Layer) ---

    public Optional<ClinicEntity> getClinicByName(String name) {
        return clinicRepository.findByName(name);
    }

    public List<ClinicEntity> getClinicsByCity(String city) {
        return clinicRepository.findByCity(city);
    }

    public List<ClinicEntity> getClinicsByPinCode(String pinCode) {
        return clinicRepository.findByPinCode(pinCode);
    }

}
