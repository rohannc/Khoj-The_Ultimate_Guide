package com.rohan.Khoj.patient;

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
public class PatientDTO {

    private UUID id; // From BaseUserEntity
    private String username; // From BaseUserEntity
    private String emailId; // From BaseUserEntity (named 'email' in registration DTO)

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;

    private String street;
    private String city;
    private String state;
    private String pinCode;
    private String country;

    private String primaryMobile;
    private String bloodGroup;

    private LocalDateTime createdAt; // From BaseUserEntity
    private LocalDateTime updatedAt; // From BaseUserEntity

}