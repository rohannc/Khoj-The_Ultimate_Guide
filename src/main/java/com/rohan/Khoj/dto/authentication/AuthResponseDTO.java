package com.rohan.Khoj.dto.authentication;

import com.rohan.Khoj.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String username;
    private UUID userId; // The UUID of the logged-in user
    private UserType userType; // The type of user (PATIENT, DOCTOR, CLINIC, ADMIN)
    private String message;
}