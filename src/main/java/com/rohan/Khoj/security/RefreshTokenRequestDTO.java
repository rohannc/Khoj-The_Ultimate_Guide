package com.rohan.Khoj.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to refresh access token")
public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token cannot be blank")
    @Schema(description = "The refresh token previously issued", example = "4a12ec584b1d42be81cf...")
    private String refreshToken;
}
