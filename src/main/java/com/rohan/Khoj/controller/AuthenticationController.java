package com.rohan.Khoj.controller;

import com.rohan.Khoj.dto.authentication.AuthRequestDTO;
import com.rohan.Khoj.dto.authentication.AuthResponseDTO;
import com.rohan.Khoj.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService; // Inject the new service

    /**
     * Authenticates a user (Patient, Doctor, or Clinic) and returns a JWT token.
     * Delegates core authentication logic to AuthService.
     *
     * @param authRequest The DTO containing username and password.
     * @return ResponseEntity with AuthResponseDTO and appropriate HTTP status.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateAndGetToken(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            AuthResponseDTO response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Specific handling for authentication failures
            System.err.println("Authentication failed for user " + authRequest.getUsername() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    AuthResponseDTO.builder().message("Invalid username or password.").build()
            );
        } catch (Exception e) {
            // Catch any other unexpected errors from the service
            System.err.println("Unexpected error during authentication for user " + authRequest.getUsername() + ": " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AuthResponseDTO.builder().message("An unexpected error occurred during login. Please try again later.").build()
            );
        }
    }
}