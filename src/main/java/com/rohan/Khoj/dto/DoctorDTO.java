package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

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
    private Set<String> specialization;
    private Set<String> qualifications;
    private Integer experienceYears;
    private String registrationNumber;

    private Set<String> phoneNumbers; // Set of phone number strings

    private LocalDate registrationIssueDate;

    private LocalDateTime createdAt; // From BaseUserEntity
    private LocalDateTime updatedAt; // From BaseUserEntity

}
