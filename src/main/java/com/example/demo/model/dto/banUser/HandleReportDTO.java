package com.example.demo.model.dto.banUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record HandleReportDTO(@Positive(message = "Invalid user id. Must be a positive number.")long reportId,
                              @NotBlank(message = "Reason shouldn't be empty or null") String reason,
                              boolean status,
                              @Positive(message = "Ban time should be a positive number.") int hoursToBan) {
}
