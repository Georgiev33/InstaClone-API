package com.example.demo.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegistrationDTO {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

}
