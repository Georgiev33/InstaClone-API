package com.example.demo.model.dto.banUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record UnbanUserDTO(@Positive(message = "Invalid user id. Must be a positive number.")long userIdToUnban,
                           @NotBlank(message = "Reason shouldn't be empty or null") String reason) {
}
