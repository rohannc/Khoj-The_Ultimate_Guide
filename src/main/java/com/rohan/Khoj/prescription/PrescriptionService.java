package com.rohan.Khoj.prescription;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ModelMapper modelMapper;

    public List<PrescriptionDTO> getPrescriptionsByPatient(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(p -> modelMapper.map(p, PrescriptionDTO.class))
                .collect(Collectors.toList());
    }
}
