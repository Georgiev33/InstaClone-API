package com.example.demo.model.dto.User;

public record UserRegistrationDTO(String username, String email, String password, String confirmPassword, String bio) {
}
