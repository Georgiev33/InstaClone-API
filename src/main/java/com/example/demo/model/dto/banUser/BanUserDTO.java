package com.example.demo.model.dto.banUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BanUserDTO(@Positive(message = "Invalid user id. Must be a positive number.") long userIdToBan,
                         @NotBlank(message = "Reason shouldn't be empty or null") String reason,
                         @Positive(message = "Ban time should be a positive number.") int hoursToBan) {
}
