package com.rohan.Khoj.dto.registration;

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
public class RegistrationResponseDTO {

    // A message indicating the status of the registration attempt (e.g., "User registered successfully!").
    private String message;

    // The authentication token (e.g., JWT) for the new user.
    // This allows the client to immediately authenticate the user without a separate login call.
    private String token;

    // The UUID of the newly registered user.
    // This allows the client to immediately identify the new user.
    private UUID id;

    // The username of the newly registered user.
    private String username;

    // The type of user that was just registered (e.g., PATIENT, DOCTOR, CLINIC, ADMIN).
    private UserType registeredUserType;
}