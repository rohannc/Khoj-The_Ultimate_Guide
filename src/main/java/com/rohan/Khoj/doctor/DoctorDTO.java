package com.rohan.Khoj.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.rohan.Khoj.common.BaseUserEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private UUID id; // From BaseUserEntity
    private String username; // From BaseUserEntity
    private String emailId; // From BaseUserEntity

    private String firstName;
    private String lastName;
    private String gender;
    private String specializations;
    private String qualifications;
    private Integer yearsOfExperience;
    private String registrationNumber;

    private String primaryMobile;

    private LocalDate registrationIssueDate;

    private LocalDateTime createdAt; // From BaseUserEntity
    private LocalDateTime updatedAt; // From BaseUserEntity

}
