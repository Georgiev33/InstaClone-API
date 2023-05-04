package com.example.demo.model.dto.user;

import jakarta.validation.constraints.*;


public record UserRegistrationDTO(
        @Size(min = 5, message = "Username must be at least 5 characters.") String username,
        @Email(message = "Valid email required. Format: example@example.com") String email,
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
                message = "Please enter a valid password that is at least 8 characters long" +
                        " and contains at least one uppercase letter, one lowercase letter, and one digit.")
        String password, @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
        message = "Please enter a valid password that is at least 8 characters long" +
                " and contains at least one uppercase letter, one lowercase letter, and one digit.")
        String confirmPassword,
       @NotNull String bio) {
}
