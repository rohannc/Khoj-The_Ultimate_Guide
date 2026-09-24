package com.rohan.Khoj.common;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class PasswordUpdateRequestDTO {
    
    @NotBlank(message = "Current password cannot be blank")
    private String currentPassword;
    
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;
}
