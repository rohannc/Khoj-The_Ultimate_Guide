package com.rohan.Khoj.service; // Or a suitable package

import com.rohan.Khoj.entity.BaseUserEntity;
import com.rohan.Khoj.entity.ClinicEntity;
import com.rohan.Khoj.entity.DoctorEntity;
import com.rohan.Khoj.entity.PatientEntity;
import com.rohan.Khoj.repository.ClinicRepository;
import com.rohan.Khoj.repository.DoctorRepository;
import com.rohan.Khoj.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Attempt to find the user in each repository
        Optional<PatientEntity> patient = patientRepository.findByUsername(username);
        if (patient.isPresent()) {
            return patient.get(); // PatientEntity already implements UserDetails
        }

        Optional<DoctorEntity> doctor = doctorRepository.findByUsername(username);
        if (doctor.isPresent()) {
            return doctor.get(); // DoctorEntity already implements UserDetails
        }

        Optional<ClinicEntity> clinic = clinicRepository.findByUsername(username);
        if (clinic.isPresent()) {
            return clinic.get(); // ClinicEntity already implements UserDetails
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}