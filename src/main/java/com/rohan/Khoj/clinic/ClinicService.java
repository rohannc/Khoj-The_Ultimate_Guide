package com.rohan.Khoj.clinic;

import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.exception.ConflictException;
import com.rohan.Khoj.exception.ResourceNotFoundException;
import com.rohan.Khoj.clinic.ClinicDTO;
import com.rohan.Khoj.clinic.ClinicUpdateRequestDTO;
import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.clinic.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.rohan.Khoj.common.PasswordUpdateRequestDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default for all read operations
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    // --- Update Operations ---

    /**
     * Updates an existing clinic's profile details.
     * Password changes are handled by the dedicated updatePassword method.
     *
     * @param id The UUID of the clinic to update.
     * @param updateRequestDTO The DTO with new profile data.
     * @return The updated ClinicDTO.
     * @throws ResourceNotFoundException if the clinic is not found.
     * @throws ConflictException if the new clinic name or email is already in use.
     */
    @Transactional
    public ClinicDTO updateClinic(UUID id, ClinicUpdateRequestDTO updateRequestDTO) {
        ClinicEntity clinicToUpdate = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        // --- Handle Clinic Name Update ---
        if (updateRequestDTO.getName() != null && !updateRequestDTO.getName().equals(clinicToUpdate.getName())) {
            if (clinicRepository.findByName(updateRequestDTO.getName()).isPresent()) {
                throw new ConflictException("A clinic with the name '" + updateRequestDTO.getName() + "' already exists.");
            }
            clinicToUpdate.setName(updateRequestDTO.getName());
        }

        // --- Handle Email Update ---
        if (updateRequestDTO.getEmailId() != null && !updateRequestDTO.getEmailId().equals(clinicToUpdate.getEmailId())) {
            if (clinicRepository.findByEmailId(updateRequestDTO.getEmailId()).isPresent()) {
                throw new ConflictException("Email '" + updateRequestDTO.getEmailId() + "' is already in use.");
            }
            clinicToUpdate.setEmailId(updateRequestDTO.getEmailId());
        }

        // --- Map other non-sensitive fields ---
        modelMapper.map(updateRequestDTO, clinicToUpdate);

        clinicToUpdate.setUpdatedAt(LocalDateTime.now());
        ClinicEntity updatedClinic = clinicRepository.save(clinicToUpdate);

        return modelMapper.map(updatedClinic, ClinicDTO.class);
    }

    /**
     * Updates the password for a specific clinic after verifying the current password.
     *
     * @param id The UUID of the clinic.
     * @param passwordRequest The DTO containing the current and new passwords.
     * @throws ResourceNotFoundException if the clinic is not found.
     * @throws BadRequestException if the current password is incorrect.
     */
    @Transactional
    public void updatePassword(UUID id, PasswordUpdateRequestDTO passwordRequest) {
        ClinicEntity clinicToUpdate = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic not found with id: " + id));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), clinicToUpdate.getPassword())) {
            throw new BadRequestException("Incorrect current password.");
        }

        clinicToUpdate.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        clinicToUpdate.setUpdatedAt(LocalDateTime.now());
        clinicRepository.save(clinicToUpdate);
    }

    // --- Retrieval Operations ---

    public List<ClinicDTO> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<ClinicDTO> getClinicById(UUID id) {
        return clinicRepository.findById(id)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public Optional<ClinicEntity> getClinicEntityById(UUID id) {
        return clinicRepository.findById(id);
    }

    public Optional<ClinicDTO> getClinicByName(String name) {
        return clinicRepository.findByName(name)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public Optional<ClinicDTO> getClinicByEmail(String email) {
        return clinicRepository.findByEmailId(email)
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class));
    }

    public List<ClinicDTO> getClinicsByCity(String city) {
        return clinicRepository.findByCityContainingIgnoreCase(city).stream()
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    public List<ClinicDTO> getClinicsByPinCode(String pinCode) {
        return clinicRepository.findByPinCode(pinCode).stream()
                .map(clinic -> modelMapper.map(clinic, ClinicDTO.class))
                .collect(Collectors.toList());
    }

    // --- Delete Operation ---

    /**
     * Deletes a clinic by its ID.
     *
     * @param id The UUID of the clinic to delete.
     * @throws ResourceNotFoundException if the clinic is not found.
     */
    @Transactional
    public void deleteClinic(UUID id) {
        if (!clinicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clinic not found with id: " + id);
        }
        clinicRepository.deleteById(id);
    }
}
