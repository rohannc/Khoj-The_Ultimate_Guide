package com.rohan.Khoj.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload to initiate forgot password flow using verified email and primary mobile")
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Schema(description = "Registered email address of the account", example = "aarav.sharma@example.com")
    private String email;

    @NotBlank(message = "Primary mobile cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Primary mobile must be exactly 10 digits")
    @Schema(description = "Registered 10-digit primary mobile number", example = "9876543210")
    private String primaryMobile;
}
