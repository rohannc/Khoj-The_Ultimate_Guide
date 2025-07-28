package com.rohan.Khoj.service;

import com.rohan.Khoj.dto.AuthRequestDTO;
import com.rohan.Khoj.dto.AuthResponseDTO;
import com.rohan.Khoj.entity.BaseUserEntity; // To cast UserDetails to get specific type information
import com.rohan.Khoj.entity.UserType; // Assuming UserType enum exists
import com.rohan.Khoj.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Good practice for services

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Our custom UserDetailsService

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     * Handles authentication logic and delegates token generation.
     *
     * @param authRequest The DTO containing username and password.
     * @return AuthResponseDTO containing login status, JWT token, and username.
     * @throws UsernameNotFoundException if the user is not found.
     * @throws BadCredentialsException if the password does not match.
     * @throws RuntimeException for other unexpected authentication errors.
     */
    @Transactional(readOnly = true) // Authentication is typically a read-only operation
    public AuthResponseDTO login(AuthRequestDTO authRequest) {
        try {
            // Step 1: Authenticate the user using Spring Security's AuthenticationManager.
            // This will trigger OurUserDetailsService.loadUserByUsername() and compare passwords.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // If authentication is successful, the 'authentication' object will be authenticated.
            // Spring Security typically throws BadCredentialsException or UsernameNotFoundException
            // if authentication fails. If it reaches here, it's authenticated.
            if (authentication.isAuthenticated()) {
                // Step 2: Load full UserDetails again to ensure we have the complete object (e.g., specific entity type)
                // This is useful if you want to add more claims to the token or return more details.
                UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

                // Step 3: Generate JWT token using the UserDetails
                String token = jwtService.generateToken(userDetails);

                // Step 4: Determine the user type for the response (optional, but good for client)
                UserType userType = null;
                if (userDetails instanceof BaseUserEntity) {
                    userType = ((BaseUserEntity) userDetails).getUserType(); // Assuming BaseUserEntity has a getUserType()
                }
                // If BaseUserEntity doesn't have getUserType(), you might need to infer it from authorities or
                // add a specific method to OurUserDetailsService to return entity with type.
                // For demonstration, let's add getUserType to BaseUserEntity or infer from Role.

                // Step 5: Build and return the response DTO
                return AuthResponseDTO.builder()
                        .message("Login successful!")
                        .token(token)
                        .username(authRequest.getUsername())
                        .userType(userType)
                        .userId(((BaseUserEntity) userDetails).getId())
                        .build();
            } else {
                // This block should theoretically not be reached as authenticate() would throw.
                // It's a fail-safe.
                throw new BadCredentialsException("Authentication failed unexpectedly for username: " + authRequest.getUsername());
            }
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // Re-throw these specific exceptions so the controller can handle them gracefully (e.g., 401 UNAUTHORIZED)
            throw e;
        } catch (Exception e) {
            // Catch any other unexpected errors during the authentication process
            throw new RuntimeException("An unexpected error occurred during authentication.", e);
        }
    }
}
