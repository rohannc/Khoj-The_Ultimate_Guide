package com.rohan.Khoj.auth;

import com.rohan.Khoj.common.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing user credentials and JWT tokens")
public class AuthResponseDTO {

    @Schema(description = "Short-lived JWT Access Token")
    private String accessToken;

    @Schema(description = "Long-lived Refresh Token for obtaining new access tokens")
    private String refreshToken;

    @Schema(description = "Username of the authenticated user")
    private String username;

    @Schema(description = "User unique ID (Patient/Doctor/Clinic ID)")
    private UUID userId;

    @Schema(description = "Type of user (PATIENT, DOCTOR, CLINIC)")
    private UserType userType;

    @Schema(description = "Status or informational message")
    private String message;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Backward-compatibility getter for frontend components expecting 'token'
     */
    public String getToken() {
        return accessToken;
    }

    /**
     * Backward-compatibility setter for callers providing 'token'
     */
    public void setToken(String token) {
        this.accessToken = token;
    }
}