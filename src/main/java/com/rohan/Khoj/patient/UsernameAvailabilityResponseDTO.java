package com.rohan.Khoj.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO indicating if a username is available and providing suggestions if it is not")
public class UsernameAvailabilityResponseDTO {

    @Schema(description = "True if the requested username is available, false otherwise", example = "false")
    private boolean available;

    @Schema(description = "A list of available suggested usernames if the requested one is taken", example = "[\"rohan2026\", \"rohan842\", \"the_rohan\"]")
    private List<String> suggestions;
}
