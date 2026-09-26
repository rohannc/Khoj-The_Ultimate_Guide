package com.rohan.Khoj.auth;

import com.rohan.Khoj.auth.AuthRequestDTO;
import com.rohan.Khoj.auth.AuthResponseDTO;
import com.rohan.Khoj.common.BaseUserEntity; // To cast UserDetails to get specific type information
import com.rohan.Khoj.common.UserType; // Assuming UserType enum exists
import com.rohan.Khoj.security.JwtService;
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

import com.rohan.Khoj.common.Role;

import java.util.UUID;

import com.rohan.Khoj.security.RefreshTokenEntity;
import com.rohan.Khoj.security.RefreshTokenService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Our custom UserDetailsService
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates a user and generates JWT access and refresh tokens upon successful login.
     * Handles authentication logic and delegates token generation.
     *
     * @param authRequest The DTO containing username and password.
     * @return AuthResponseDTO containing login status, tokens, and user details.
     * @throws UsernameNotFoundException if the user is not found.
     * @throws BadCredentialsException if the password does not match.
     * @throws RuntimeException for other unexpected authentication errors.
     */
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO authRequest) {
        try {
            // Step 1: Authenticate the user using Spring Security's AuthenticationManager.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

                // Step 2: Generate short-lived Access Token
                String accessToken = jwtService.generateToken(userDetails);

                // Step 3: Determine user type and user ID
                UserType userType = null;
                UUID userId = null;
                if (userDetails instanceof BaseUserEntity) {
                    BaseUserEntity baseUser = (BaseUserEntity) userDetails;
                    userType = baseUser.getUserType();
                    userId = baseUser.getId();
                }

                // Step 4: Create and persist Refresh Token
                RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(authRequest.getUsername(), userId);

                // Step 5: Build and return the response DTO
                return AuthResponseDTO.builder()
                        .message("Login successful!")
                        .accessToken(accessToken)
                        .refreshToken(refreshTokenEntity.getToken())
                        .username(authRequest.getUsername())
                        .userType(userType)
                        .userId(userId)
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
