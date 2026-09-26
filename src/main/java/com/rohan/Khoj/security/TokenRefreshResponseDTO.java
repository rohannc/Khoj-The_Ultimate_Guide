package com.rohan.Khoj.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload after refreshing access token")
public class TokenRefreshResponseDTO {

    @Schema(description = "New short-lived JWT access token")
    private String accessToken;

    @Schema(description = "New rotated refresh token")
    private String refreshToken;

    @Builder.Default
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiry in seconds", example = "900")
    private Long expiresIn;
}
