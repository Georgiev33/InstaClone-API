package com.example.demo.model.dto;
public record UserLoginDTO(String username, String password){
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
