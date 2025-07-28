package com.rohan.Khoj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDTO {

    private UUID id; // From BaseUserEntity
    private String username; // From BaseUserEntity
    private String emailId; // From BaseUserEntity

    private String name; // Corresponds to clinicName from registration DTO
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private String country;

    private Set<String> phoneNumbers; // Set of phone number strings
    private String website;

    private Map<String, String> openingHours; // e.g., {"Monday": "9AM-5PM"}

    private LocalDateTime createdAt; // From BaseUserEntity
    private LocalDateTime updatedAt; // From BaseUserEntity

}