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

    private Set<String> phoneNumbers; // Set of phone number strings
    private String bloodGroup;

    private LocalDateTime createdAt; // From BaseUserEntity
    private LocalDateTime updatedAt; // From BaseUserEntity

}