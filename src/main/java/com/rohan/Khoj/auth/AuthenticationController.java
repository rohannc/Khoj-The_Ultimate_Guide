package com.rohan.Khoj.auth;

import com.rohan.Khoj.auth.AuthRequestDTO;
import com.rohan.Khoj.auth.AuthResponseDTO;
import com.rohan.Khoj.auth.AuthService;
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

import com.rohan.Khoj.security.RefreshTokenRequestDTO;
import com.rohan.Khoj.security.RefreshTokenService;
import com.rohan.Khoj.security.TokenRefreshResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, access token refresh, logout, and password recovery (forgot/reset password)")
public class AuthenticationController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    /**
     * Authenticates a user (Patient, Doctor, or Clinic) and returns JWT access and refresh tokens.
     * Delegates core authentication logic to AuthService.
     *
     * @param authRequest The DTO containing username and password.
     * @return ResponseEntity with AuthResponseDTO and appropriate HTTP status.
     */
    @Operation(summary = "Authenticate user and get access & refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateAndGetToken(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            AuthResponseDTO response = authService.login(authRequest);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            System.err.println("Authentication failed for user " + authRequest.getUsername() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    AuthResponseDTO.builder().message("Invalid username or password.").build()
            );
        } catch (Exception e) {
            System.err.println("Unexpected error during authentication for user " + authRequest.getUsername() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AuthResponseDTO.builder().message("An unexpected error occurred during login. Please try again later.").build()
            );
        }
    }

    /**
     * Refreshes the short-lived access token using a valid, non-revoked refresh token.
     * Automatically rotates the refresh token to prevent replay attacks.
     *
     * @param request The RefreshTokenRequestDTO containing the current refresh token.
     * @return ResponseEntity with TokenRefreshResponseDTO containing new tokens.
     */
    @Operation(summary = "Refresh short-lived access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Access token successfully refreshed and rotated")
    @ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            TokenRefreshResponseDTO response = refreshTokenService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Could not refresh token: " + e.getMessage())
            );
        }
    }

    /**
     * Revokes the provided refresh token to securely log the user out.
     *
     * @param request The RefreshTokenRequestDTO containing the refresh token to invalidate.
     * @return 200 OK confirming logout.
     */
    @Operation(summary = "Revoke refresh token and log out")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            refreshTokenService.revokeToken(request.getRefreshToken());
            return ResponseEntity.ok(Map.of("message", "Logged out successfully. Refresh token revoked."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Logged out."));
        }
    }

    /**
     * Initiates the forgot password flow by verifying matching registered email and primary mobile number.
     * Generates a 6-digit OTP valid for 10 minutes.
     *
     * @param request The ForgotPasswordRequestDTO containing email and primaryMobile.
     * @return 200 OK with confirmation message.
     */
    @Operation(
            summary = "Initiate forgot password request",
            description = "Verifies identity using registered email and primary mobile. If valid, generates a 6-digit OTP valid for 10 minutes."
    )
    @ApiResponse(responseCode = "200", description = "Password reset request processed")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        passwordResetService.initiateForgotPassword(request);
        return ResponseEntity.ok(Map.of(
                "message", "If the provided email and primary mobile match an active account, a 6-digit OTP has been sent."
        ));
    }

    /**
     * Resets the account password using the verified 6-digit OTP.
     * Encrypts the new password and automatically revokes all active refresh tokens for security.
     *
     * @param request The ResetPasswordRequestDTO containing email, otp, and newPassword.
     * @return 200 OK confirming successful password update.
     */
    @Operation(
            summary = "Reset password with OTP",
            description = "Verifies the 6-digit OTP and updates the account password. Automatically revokes existing sessions for security."
    )
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP, or too many failed attempts")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of(
                "message", "Password has been reset successfully. Please log in with your new password."
        ));
    }
}
