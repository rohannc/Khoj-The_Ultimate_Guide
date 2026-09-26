package com.rohan.Khoj.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private UUID id;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String title;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String message;
    @NotBlank(message = "Field cannot be blank")

    @Schema(description = "Details about the field")
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
