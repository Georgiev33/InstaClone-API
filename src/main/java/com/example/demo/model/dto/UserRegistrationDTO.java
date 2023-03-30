package com.example.demo.model.dto;

public record UserRegistrationDTO(String username, String email, String password, String confirmPassword) {
    public String getEmail(){
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
