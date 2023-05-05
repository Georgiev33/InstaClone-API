package com.example.demo.model.dto.ReportedUsers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ReportUserDTO(@Positive(message = "Invalid user id. Must be a positive number.")int reportedId,
                            @NotBlank(message = "Reason shouldn't be empty or null") String reason) {
}
